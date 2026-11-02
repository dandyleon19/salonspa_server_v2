package com.danydandy.SalonSpa.domain.ports.out;

import com.danydandy.SalonSpa.domain.model.Plan;
import com.danydandy.SalonSpa.domain.model.SalonSubscription;
import com.danydandy.SalonSpa.domain.model.SubscriptionEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SubscriptionRepositoryPort {

    Flux<Plan> findPlans();

    Mono<Plan> findPlanByCode(String code);

    /** The stored subscription of a salon (empty if it has none yet). The plan comes with its features. */
    Mono<SalonSubscription> findBySalonId(Long salonId);

    /** Every salon with its stored subscription; salons without one come back with a null plan. */
    Flux<SalonSubscription> findAllWithSalonName();

    /** Insert or update by salon id. */
    Mono<Void> save(SalonSubscription subscription);

    Mono<Void> saveEvent(SubscriptionEvent event);

    Flux<SubscriptionEvent> findEvents(Long salonId, int limit);
}
