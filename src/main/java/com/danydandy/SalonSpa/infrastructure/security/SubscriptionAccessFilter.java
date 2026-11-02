package com.danydandy.SalonSpa.infrastructure.security;

import com.danydandy.SalonSpa.application.security.SecurityHelper;
import com.danydandy.SalonSpa.domain.exception.NotFoundException;
import com.danydandy.SalonSpa.domain.exception.PlanRequiredException;
import com.danydandy.SalonSpa.domain.exception.SubscriptionInactiveException;
import com.danydandy.SalonSpa.domain.model.AuthUser;
import com.danydandy.SalonSpa.domain.model.SalonSubscription;
import com.danydandy.SalonSpa.domain.model.SubscriptionStatus;
import com.danydandy.SalonSpa.domain.ports.in.SubscriptionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;

/**
 * Enforces the subscription on every API call, after the user has been authenticated and authorized:
 * <ul>
 *   <li>a salon whose subscription is blocked (trial/payment over, or suspended) gets 402 SUBSCRIPTION_INACTIVE;</li>
 *   <li>a route that belongs to a feature the salon's plan does not include gets 403 PLAN_REQUIRED.</li>
 * </ul>
 * SUPER_ADMIN is never limited. Sign-in, the public catalog and the subscription endpoints stay open so a blocked
 * salon can still log in and see why it is blocked.
 */
@RequiredArgsConstructor
public class SubscriptionAccessFilter implements WebFilter {

    private final SubscriptionUseCase subscriptionUseCase;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        HttpMethod method = exchange.getRequest().getMethod();

        if (isExempt(path, method)) {
            return chain.filter(exchange);
        }

        return currentAuthUser()
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty())
                .flatMap(optionalUser -> {
                    // Not signed in (public route) or no salon to check: nothing to enforce here.
                    if (optionalUser.isEmpty() || SecurityHelper.isSuperAdmin(optionalUser.get())
                            || optionalUser.get().getSalonId() == null) {
                        return chain.filter(exchange);
                    }
                    return subscriptionUseCase.getEffective(optionalUser.get().getSalonId())
                            .map(Optional::of)
                            .onErrorReturn(NotFoundException.class, Optional.empty())
                            .flatMap(optionalSubscription -> optionalSubscription.isEmpty()
                                    ? chain.filter(exchange)
                                    : enforce(optionalSubscription.get(), method, path, exchange, chain));
                });
    }

    private Mono<Void> enforce(
            SalonSubscription subscription,
            HttpMethod method,
            String path,
            ServerWebExchange exchange,
            WebFilterChain chain
    ) {
        if (subscription.isBlocked()) {
            String message = subscription.getStatus() == SubscriptionStatus.SUSPENDED
                    ? "Tu suscripción está suspendida. Contacta con soporte para reactivarla."
                    : "Tu suscripción venció. Renueva tu plan para seguir usando el sistema.";
            return reject(exchange, HttpStatus.PAYMENT_REQUIRED, SubscriptionInactiveException.CODE, message);
        }

        String feature = FeatureRoutes.requiredFeature(method, path);
        if (feature != null && !subscription.hasFeature(feature)) {
            String message = "Tu plan " + subscription.getPlan().getName()
                    + " no incluye esta función. Mejora tu plan para usarla.";
            return reject(exchange, HttpStatus.FORBIDDEN, PlanRequiredException.CODE, message);
        }
        return chain.filter(exchange);
    }

    private static Mono<AuthUser> currentAuthUser() {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(context -> Mono.justOrEmpty(context.getAuthentication()))
                .map(Authentication::getPrincipal)
                .filter(AuthUser.class::isInstance)
                .cast(AuthUser.class);
    }

    private static boolean isExempt(String path, HttpMethod method) {
        return HttpMethod.OPTIONS.equals(method)
                || !path.startsWith("/api/")
                || path.startsWith("/api/auth/")
                || path.startsWith("/api/public/")
                || path.equals("/api/subscription/me")
                || path.equals("/api/plans");
    }

    /** Same JSON shape as GlobalExceptionHandler, written by hand because filters run outside controller advice. */
    private static Mono<Void> reject(ServerWebExchange exchange, HttpStatus status, String code, String message) {
        String body = "{\"timestamp\":\"" + Instant.now() + "\""
                + ",\"status\":" + status.value()
                + ",\"error\":\"" + status.getReasonPhrase() + "\""
                + ",\"code\":\"" + code + "\""
                + ",\"message\":\"" + escapeJson(message) + "\""
                + ",\"path\":\"" + escapeJson(exchange.getRequest().getPath().value()) + "\"}";

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
