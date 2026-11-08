package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.application.dto.request.ActivateSubscriptionRequest;
import com.danydandy.SalonSpa.application.dto.request.ExtendTrialRequest;
import com.danydandy.SalonSpa.application.dto.request.SetTrialDaysRequest;
import com.danydandy.SalonSpa.application.dto.request.SuspendSubscriptionRequest;
import com.danydandy.SalonSpa.application.dto.response.PlanResponse;
import com.danydandy.SalonSpa.application.dto.response.SubscriptionEventResponse;
import com.danydandy.SalonSpa.application.dto.response.SubscriptionResponse;
import com.danydandy.SalonSpa.application.security.SecurityHelper;
import com.danydandy.SalonSpa.domain.exception.BadRequestException;
import com.danydandy.SalonSpa.domain.exception.NotFoundException;
import com.danydandy.SalonSpa.domain.ports.in.SubscriptionUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * /api/subscription/me and /api/plans are open to any signed-in user; everything under /api/subscriptions is
 * SUPER_ADMIN only (see SecurityConfig).
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class SubscriptionController {

    private final SubscriptionUseCase subscriptionUseCase;

    @GetMapping("/subscription/me")
    public Mono<ResponseEntity<SubscriptionResponse>> getMine() {
        return SecurityHelper.currentUser().flatMap(authUser -> {
            if (SecurityHelper.isSuperAdmin(authUser)) {
                return Mono.just(SubscriptionResponse.forSuperAdmin());
            }
            if (authUser.getSalonId() == null) {
                return Mono.<SubscriptionResponse>error(NotFoundException.forResource("Salon", null));
            }
            // Opening the app is what starts a pending trial's days (first sign-in of any user of the salon).
            return subscriptionUseCase.startTrialClock(authUser.getSalonId()).map(SubscriptionResponse::from);
        }).map(ResponseEntity::ok);
    }

    @GetMapping("/plans")
    public Mono<ResponseEntity<List<PlanResponse>>> getPlans() {
        return subscriptionUseCase.listPlans()
                .map(PlanResponse::from)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/subscriptions")
    public Mono<ResponseEntity<List<SubscriptionResponse>>> getAll() {
        return subscriptionUseCase.listAll()
                .map(SubscriptionResponse::from)
                .collectList()
                .map(ResponseEntity::ok);
    }

    @PostMapping("/subscriptions/{salonId}/activate")
    public Mono<ResponseEntity<SubscriptionResponse>> activate(
            @PathVariable @Positive Long salonId,
            @Valid @RequestBody ActivateSubscriptionRequest request
    ) {
        boolean noExpiry = Boolean.TRUE.equals(request.getNoExpiry());
        if (!noExpiry && request.getPaidUntil() == null) {
            return Mono.error(new BadRequestException("Indica hasta cuándo está pagado, o marca que el plan no vence"));
        }
        return subscriptionUseCase.activate(salonId, request.getPlanCode(), noExpiry ? null : request.getPaidUntil())
                .map(SubscriptionResponse::from)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/subscriptions/{salonId}/trial-days")
    public Mono<ResponseEntity<SubscriptionResponse>> setTrialDays(
            @PathVariable @Positive Long salonId,
            @Valid @RequestBody SetTrialDaysRequest request
    ) {
        return subscriptionUseCase.setTrialDays(salonId, request.getDays())
                .map(SubscriptionResponse::from)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/subscriptions/{salonId}/extend-trial")
    public Mono<ResponseEntity<SubscriptionResponse>> extendTrial(
            @PathVariable @Positive Long salonId,
            @Valid @RequestBody ExtendTrialRequest request
    ) {
        return subscriptionUseCase.extendTrial(salonId, request.getDays())
                .map(SubscriptionResponse::from)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/subscriptions/{salonId}/suspend")
    public Mono<ResponseEntity<SubscriptionResponse>> suspend(
            @PathVariable @Positive Long salonId,
            @Valid @RequestBody SuspendSubscriptionRequest request
    ) {
        return subscriptionUseCase.suspend(salonId, request.getReason())
                .map(SubscriptionResponse::from)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/subscriptions/{salonId}/resume")
    public Mono<ResponseEntity<SubscriptionResponse>> resume(@PathVariable @Positive Long salonId) {
        return subscriptionUseCase.resume(salonId)
                .map(SubscriptionResponse::from)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/subscriptions/{salonId}/events")
    public Mono<ResponseEntity<List<SubscriptionEventResponse>>> getEvents(@PathVariable @Positive Long salonId) {
        return subscriptionUseCase.findEvents(salonId)
                .map(SubscriptionEventResponse::from)
                .collectList()
                .map(ResponseEntity::ok);
    }
}
