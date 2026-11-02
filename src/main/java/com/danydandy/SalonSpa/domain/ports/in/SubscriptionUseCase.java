package com.danydandy.SalonSpa.domain.ports.in;

import com.danydandy.SalonSpa.domain.model.Plan;
import com.danydandy.SalonSpa.domain.model.SalonSubscription;
import com.danydandy.SalonSpa.domain.model.SubscriptionEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface SubscriptionUseCase {

    /** The subscription of a salon with its effective state. A salon that has none starts its trial here. */
    Mono<SalonSubscription> getEffective(Long salonId);

    /** Starts the free trial of a newly created salon. Does nothing if it already has a subscription. */
    Mono<SalonSubscription> startTrial(Long salonId);

    /**
     * Called when a user of the salon opens the app: if the trial has not started yet, its days start counting now.
     * Returns the subscription with its effective state either way.
     */
    Mono<SalonSubscription> startTrialClock(Long salonId);

    /** Fails with PLAN_LIMIT_REACHED when adding one more would pass the plan's limit. */
    Mono<Void> requireWithinLimit(Long salonId, String limitKey, long currentCount);

    Flux<Plan> listPlans();

    /** SUPER_ADMIN: every salon with its subscription. */
    Flux<SalonSubscription> listAll();

    /**
     * SUPER_ADMIN: sets the paid plan of a salon and the day it is paid until ({@code paidUntil} null = never
     * expires, for a salon that does not pay). It SETS the date, it does not add to it, so repeating the same call
     * changes nothing and a wrong date is fixed by sending the right one.
     */
    Mono<SalonSubscription> activate(Long salonId, String planCode, LocalDate paidUntil);

    /** SUPER_ADMIN: gives more trial days (counted from today, or from the current end if it is still running). */
    Mono<SalonSubscription> extendTrial(Long salonId, int days);

    /**
     * SUPER_ADMIN: sets the total length of this salon's trial. Once the trial has started its end moves to
     * start + days; before that it only changes how long the trial will last.
     */
    Mono<SalonSubscription> setTrialDays(Long salonId, int days);

    /** SUPER_ADMIN: blocks a salon by hand. Its trial / paid state and dates are kept as they are. */
    Mono<SalonSubscription> suspend(Long salonId, String reason);

    /** SUPER_ADMIN: lifts a manual suspension; the salon goes back to the state it had before. */
    Mono<SalonSubscription> resume(Long salonId);

    Flux<SubscriptionEvent> findEvents(Long salonId);

    String LIMIT_BRANCHES = "max_branches";
    String LIMIT_USERS = "max_users";
}
