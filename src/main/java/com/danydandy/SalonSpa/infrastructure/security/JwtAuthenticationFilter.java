package com.danydandy.SalonSpa.infrastructure.security;

import com.danydandy.SalonSpa.domain.model.AuthUser;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class JwtAuthenticationFilter extends AuthenticationWebFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {

        super(new ReactiveAuthenticationManager() {
            @Override
            public Mono<Authentication> authenticate(Authentication authentication) {
                return Mono.just(authentication);
            }
        });

        this.jwtService = jwtService;

        this.setServerAuthenticationConverter(this::convert);
        this.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());
    }

    private Mono<Authentication> convert(ServerWebExchange exchange) {

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.empty();
        }

        String token = authHeader.substring(7);

        if (!jwtService.isValid(token)) return Mono.empty();

        Long userId = jwtService.extractUserId(token);
        Long salonId = jwtService.extractSalonId(token);

        return Mono.just(
                new UsernamePasswordAuthenticationToken(
                        new AuthUser(userId, salonId),
                        null,
                        AuthorityUtils.NO_AUTHORITIES
                )
        );
    }
}