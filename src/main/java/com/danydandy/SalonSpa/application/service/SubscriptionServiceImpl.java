package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.application.security.SecurityHelper;
import com.danydandy.SalonSpa.config.properties.SubscriptionProperties;
import com.danydandy.SalonSpa.domain.exception.BadRequestException;
import com.danydandy.SalonSpa.domain.exception.ConflictException;
import com.danydandy.SalonSpa.domain.exception.NotFoundException;
import com.danydandy.SalonSpa.domain.exception.PlanLimitException;
import com.danydandy.SalonSpa.domain.model.AuthUser;
import com.danydandy.SalonSpa.domain.model.Plan;
import com.danydandy.SalonSpa.domain.model.SalonSubscription;
import com.danydandy.SalonSpa.domain.model.SubscriptionEvent;
import com.danydandy.SalonSpa.domain.model.SubscriptionStatus;
import com.danydandy.SalonSpa.domain.ports.in.SubscriptionUseCase;
import com.danydandy.SalonSpa.domain.ports.out.SalonRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.SubscriptionRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Subscription rules for salons.
 * <p>
 * The database stores TRIAL, ACTIVE or SUSPENDED plus the dates; PAST_DUE and EXPIRED are derived from the dates every
 * time a subscription is read, so nothing needs a scheduled job to "expire" a salon. The stored row is cached for a few
 * seconds because the access filter asks for it on every request; every write drops the cached copy.
 */
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionUseCase {

    private static final long CACHE_TTL_MS = 30_000;
    private static final int EVENTS_LIMIT = 50;
    private static final int MAX_TRIAL_DAYS = 365;
    private static final int MAX_PAID_YEARS = 5;

    private final SubscriptionRepositoryPort subscriptionRepositoryPort;
    private final SalonRepositoryPort salonRepositoryPort;
    private final SubscriptionProperties properties;

    private final ConcurrentHashMap<Long, CachedEntry> cache = new ConcurrentHashMap<>();

    private record CachedEntry(SalonSubscription stored, long expiresAt) {
    }

    // ------------------------------------------------------------------ reads

    @Override
    public Mono<SalonSubscription> getEffective(Long salonId) {
        return loadStored(salonId).map(this::withEffectiveState);
    }

    @Override
    public Mono<SalonSubscription> startTrial(Long salonId) {
        return subscriptionRepositoryPort.findBySalonId(salonId)
                .switchIfEmpty(Mono.defer(() -> createTrial(salonId)))
                .map(this::withEffectiveState);
    }

    @Override
    public Mono<SalonSubscription> startTrialClock(Long salonId) {
        return loadStored(salonId).flatMap(stored -> {
            // A suspended salon cannot use the app, so its trial days do not start counting.
            if (!stored.isTrialPending() || stored.isSuspended()) {
                return Mono.just(withEffectiveState(stored));
            }
            LocalDateTime now = LocalDateTime.now();
            int days = trialDaysOf(stored);
            LocalDateTime trialEnd = endOfDay(now.toLocalDate().plusDays(days));

            SalonSubscription updated = copyOf(stored);
            updated.setTrialStartedAt(now);
            updated.setTrialEndsAt(trialEnd);
            return persist(updated, "TRIAL_STARTED", days + " día(s) · hasta " + trialEnd.toLocalDate());
        });
    }

    @Override
    public Mono<Void> requireWithinLimit(Long salonId, String limitKey, long currentCount) {
        return getEffective(salonId).flatMap(subscription -> {
            Plan plan = subscription.getPlan();
            Integer max = LIMIT_BRANCHES.equals(limitKey) ? plan.getMaxBranches()
                    : LIMIT_USERS.equals(limitKey) ? plan.getMaxUsers()
                    : null;
            if (max != null && currentCount >= max) {
                String what = LIMIT_BRANCHES.equals(limitKey) ? "sucursales" : "usuarios";
                return Mono.error(new PlanLimitException(
                        "Tu plan " + plan.getName() + " permite hasta " + max + " " + what
                                + ". Mejora tu plan para agregar más."));
            }
            return Mono.empty();
        });
    }

    @Override
    public Flux<Plan> listPlans() {
        return subscriptionRepositoryPort.findPlans();
    }

    @Override
    public Flux<SalonSubscription> listAll() {
        return subscriptionRepositoryPort.findAllWithSalonName()
                .concatMap(row -> {
                    if (row.getPlan() == null) {
                        // A salon that never had a subscription (created outside the usual flow): its trial starts now.
                        return startTrial(row.getSalonId()).map(started -> {
                            started.setSalonName(row.getSalonName());
                            return started;
                        });
                    }
                    return Mono.just(withEffectiveState(row));
                });
    }

    @Override
    public Flux<SubscriptionEvent> findEvents(Long salonId) {
        return subscriptionRepositoryPort.findEvents(salonId, EVENTS_LIMIT);
    }

    // ------------------------------------------------------------------ writes (SUPER_ADMIN)

    @Override
    public Mono<SalonSubscription> activate(Long salonId, String planCode, LocalDate paidUntil) {
        if (paidUntil != null) {
            LocalDate today = LocalDate.now();
            if (paidUntil.isBefore(today)) {
                return Mono.error(new BadRequestException("The paid-until date cannot be before today"));
            }
            if (paidUntil.isAfter(today.plusYears(MAX_PAID_YEARS))) {
                return Mono.error(new BadRequestException(
                        "The paid-until date cannot be more than " + MAX_PAID_YEARS + " years away"));
            }
        }
        return Mono.zip(loadStored(salonId), findPlan(planCode))
                .flatMap(tuple -> {
                    SalonSubscription stored = tuple.getT1();
                    Plan plan = tuple.getT2();
                    LocalDateTime newEnd = paidUntil == null ? null : endOfDay(paidUntil);

                    boolean wasActive = stored.getStoredStatus() == SubscriptionStatus.ACTIVE;
                    LocalDateTime oldEnd = stored.getCurrentPeriodEnd();
                    boolean samePlan = stored.getPlan() != null && stored.getPlan().getId().equals(plan.getId());
                    boolean sameEnd = oldEnd == null ? newEnd == null
                            : newEnd != null && oldEnd.toLocalDate().equals(newEnd.toLocalDate());
                    if (wasActive && samePlan && sameEnd) {
                        // Nothing would change: no write and no new line in the history.
                        return Mono.just(withEffectiveState(stored));
                    }

                    SalonSubscription updated = copyOf(stored);
                    updated.setPlan(plan);
                    updated.setStoredStatus(SubscriptionStatus.ACTIVE);
                    updated.setCurrentPeriodEnd(newEnd);
                    updated.setTrialEndsAt(null);
                    // Suspension is not touched here: it is lifted with resume.

                    String until = newEnd == null ? "sin vencimiento" : "hasta " + newEnd.toLocalDate();
                    String before = !wasActive ? ""
                            : oldEnd == null ? " (antes: sin vencimiento)" : " (antes: " + oldEnd.toLocalDate() + ")";
                    return persist(updated, wasActive ? "PLAN_UPDATED" : "ACTIVATED",
                            "Plan " + plan.getName() + " · " + until + before);
                });
    }

    @Override
    public Mono<SalonSubscription> extendTrial(Long salonId, int days) {
        if (days < 1) {
            return Mono.error(new BadRequestException("Days must be at least 1"));
        }
        return loadStored(salonId).flatMap(stored -> {
            SalonSubscription current = withEffectiveState(stored);
            if (current.getStatus() == SubscriptionStatus.ACTIVE) {
                return Mono.error(new ConflictException(
                        "This salon has a paid plan running; use activate to extend it"));
            }
            SalonSubscription updated = copyOf(stored);
            updated.setStoredStatus(SubscriptionStatus.TRIAL);
            updated.setCurrentPeriodEnd(null);
            updated.setNotes(null);

            if (stored.isTrialPending()) {
                // The days have not started counting: the trial just becomes longer.
                int total = Math.min(MAX_TRIAL_DAYS, trialDaysOf(stored) + days);
                updated.setTrialDays(total);
                return persist(updated, "TRIAL_EXTENDED",
                        "+" + days + " día(s) · prueba de " + total + " día(s), empieza al primer ingreso");
            }

            LocalDateTime now = LocalDateTime.now();
            boolean trialRunning = stored.getStoredStatus() == SubscriptionStatus.TRIAL
                    && stored.getTrialEndsAt() != null
                    && stored.getTrialEndsAt().isAfter(now);
            LocalDate base = trialRunning ? stored.getTrialEndsAt().toLocalDate() : LocalDate.now();
            LocalDateTime newEnd = endOfDay(base.plusDays(days));
            // Keep "end = start + trial days" true, so changing the trial length later starts from the right place.
            LocalDateTime startedAt = trialRunning && stored.getTrialStartedAt() != null ? stored.getTrialStartedAt() : now;

            updated.setTrialStartedAt(startedAt);
            updated.setTrialEndsAt(newEnd);
            updated.setTrialDays(trialLength(startedAt, newEnd));
            return persist(updated, "TRIAL_EXTENDED", "+" + days + " día(s) · hasta " + newEnd.toLocalDate());
        });
    }

    @Override
    public Mono<SalonSubscription> setTrialDays(Long salonId, int days) {
        if (days < 1 || days > MAX_TRIAL_DAYS) {
            return Mono.error(new BadRequestException("Days must be between 1 and " + MAX_TRIAL_DAYS));
        }
        return loadStored(salonId).flatMap(stored -> {
            if (stored.getStoredStatus() != SubscriptionStatus.TRIAL) {
                return Mono.error(new ConflictException(
                        "Trial days can only be changed while the salon is on trial"));
            }
            SalonSubscription updated = copyOf(stored);
            updated.setTrialDays(days);

            if (stored.getTrialStartedAt() == null) {
                return persist(updated, "TRIAL_DAYS_CHANGED",
                        "Prueba de " + days + " día(s), empieza al primer ingreso");
            }
            LocalDateTime newEnd = endOfDay(stored.getTrialStartedAt().toLocalDate().plusDays(days));
            updated.setTrialEndsAt(newEnd);
            return persist(updated, "TRIAL_DAYS_CHANGED",
                    "Prueba de " + days + " día(s) · termina el " + newEnd.toLocalDate());
        });
    }

    @Override
    public Mono<SalonSubscription> suspend(Long salonId, String reason) {
        return loadStored(salonId).flatMap(stored -> {
            if (stored.isSuspended()) {
                return Mono.just(withEffectiveState(stored));
            }
            // Only the flag changes: the trial / paid state and its dates stay as they are.
            SalonSubscription updated = copyOf(stored);
            updated.setSuspended(true);
            updated.setNotes(reason == null || reason.isBlank() ? null : reason.trim());
            return persist(updated, "SUSPENDED", updated.getNotes());
        });
    }

    @Override
    public Mono<SalonSubscription> resume(Long salonId) {
        return loadStored(salonId).flatMap(stored -> {
            if (!stored.isSuspended()) {
                return Mono.just(withEffectiveState(stored));
            }
            SalonSubscription updated = copyOf(stored);
            updated.setSuspended(false);
            updated.setNotes(null);
            return persist(updated, "RESUMED", null);
        });
    }

    // ------------------------------------------------------------------ internals

    private Mono<SalonSubscription> persist(SalonSubscription updated, String eventType, String detail) {
        Long salonId = updated.getSalonId();
        return subscriptionRepositoryPort.save(updated)
                .then(currentUserId())
                .flatMap(userId -> subscriptionRepositoryPort.saveEvent(SubscriptionEvent.builder()
                        .salonId(salonId)
                        .eventType(eventType)
                        .detail(detail)
                        .userId(userId.orElse(null))
                        .build()))
                // Drop the cached copy BEFORE reading it back, or the answer would still show the old state.
                .then(Mono.fromRunnable(() -> cache.remove(salonId)))
                .then(Mono.defer(() -> getEffective(salonId)));
    }

    private Mono<Optional<Long>> currentUserId() {
        return SecurityHelper.currentUser()
                .map(AuthUser::getUserId)
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty());
    }

    private Mono<Plan> findPlan(String planCode) {
        return subscriptionRepositoryPort.findPlanByCode(planCode == null ? "" : planCode.trim().toUpperCase())
                .switchIfEmpty(Mono.error(new BadRequestException("Unknown plan: " + planCode)));
    }

    /** The stored subscription, from cache when fresh; a salon that has none starts its trial. */
    private Mono<SalonSubscription> loadStored(Long salonId) {
        CachedEntry cached = cache.get(salonId);
        if (cached != null && cached.expiresAt() > System.currentTimeMillis()) {
            return Mono.just(cached.stored());
        }
        return subscriptionRepositoryPort.findBySalonId(salonId)
                .switchIfEmpty(Mono.defer(() -> createTrial(salonId)))
                .doOnNext(stored -> cache.put(salonId,
                        new CachedEntry(stored, System.currentTimeMillis() + CACHE_TTL_MS)));
    }

    private Mono<SalonSubscription> createTrial(Long salonId) {
        return salonRepositoryPort.findById(salonId)
                .switchIfEmpty(Mono.error(NotFoundException.forResource("Salon", salonId)))
                .then(findTrialPlan())
                .flatMap(plan -> {
                    // The days do not run yet: they start the first time a user of the salon opens the app.
                    SalonSubscription trial = SalonSubscription.builder()
                            .salonId(salonId)
                            .plan(plan)
                            .storedStatus(SubscriptionStatus.TRIAL)
                            .trialDays(properties.trialDays())
                            .build();
                    return subscriptionRepositoryPort.save(trial)
                            .then(subscriptionRepositoryPort.saveEvent(SubscriptionEvent.builder()
                                    .salonId(salonId)
                                    .eventType("TRIAL_CREATED")
                                    .detail("Prueba de " + properties.trialDays() + " día(s), empieza al primer ingreso")
                                    .build()))
                            .thenReturn(trial);
                });
    }

    /** This salon's trial length, falling back to the default when the row has none. */
    private int trialDaysOf(SalonSubscription subscription) {
        Integer days = subscription.getTrialDays();
        return days != null && days > 0 ? days : properties.trialDays();
    }

    private static int trialLength(LocalDateTime start, LocalDateTime end) {
        long days = ChronoUnit.DAYS.between(start.toLocalDate(), end.toLocalDate());
        return (int) Math.max(1, Math.min(MAX_TRIAL_DAYS, days));
    }

    private Mono<Plan> findTrialPlan() {
        return subscriptionRepositoryPort.findPlanByCode(properties.trialPlanCode())
                .switchIfEmpty(Mono.error(new ConflictException(
                        "Trial plan '" + properties.trialPlanCode() + "' does not exist")));
    }

    /**
     * Works out PAST_DUE / EXPIRED / SUSPENDED and the days left. Always returns a new object: the cached one is shared.
     * A suspended salon keeps the days left of the state it will return to, and says which one in resumeStatus.
     */
    private SalonSubscription withEffectiveState(SalonSubscription stored) {
        SalonSubscription result = copyOf(stored);
        LocalDateTime now = LocalDateTime.now();
        SubscriptionStatus status;
        LocalDateTime graceEnd = null;
        Integer daysRemaining = null;

        if (stored.getStoredStatus() == SubscriptionStatus.TRIAL) {
            LocalDateTime end = stored.getTrialEndsAt();
            if (end == null || !now.isAfter(end)) {
                status = SubscriptionStatus.TRIAL;
                daysRemaining = end == null ? null : daysUntil(now, end);
            } else {
                status = SubscriptionStatus.EXPIRED;
                daysRemaining = 0;
            }
        } else {
            LocalDateTime end = stored.getCurrentPeriodEnd();
            if (end == null || !now.isAfter(end)) {
                status = SubscriptionStatus.ACTIVE;
                daysRemaining = end == null ? null : daysUntil(now, end);
            } else {
                graceEnd = endOfDay(end.toLocalDate().plusDays(properties.graceDays()));
                if (!now.isAfter(graceEnd)) {
                    status = SubscriptionStatus.PAST_DUE;
                    daysRemaining = daysUntil(now, graceEnd);
                } else {
                    status = SubscriptionStatus.EXPIRED;
                    daysRemaining = 0;
                }
            }
        }

        if (stored.isSuspended()) {
            result.setResumeStatus(status);
            status = SubscriptionStatus.SUSPENDED;
        }

        result.setStatus(status);
        result.setGraceEndsAt(graceEnd);
        result.setDaysRemaining(daysRemaining);
        return result;
    }

    private static int daysUntil(LocalDateTime now, LocalDateTime end) {
        return (int) Math.max(0, ChronoUnit.DAYS.between(now.toLocalDate(), end.toLocalDate()));
    }

    private static LocalDateTime endOfDay(LocalDate date) {
        return date.atTime(LocalTime.of(23, 59, 59));
    }

    private static SalonSubscription copyOf(SalonSubscription source) {
        return SalonSubscription.builder()
                .salonId(source.getSalonId())
                .salonName(source.getSalonName())
                .plan(source.getPlan())
                .storedStatus(source.getStoredStatus())
                .trialDays(source.getTrialDays())
                .trialStartedAt(source.getTrialStartedAt())
                .trialEndsAt(source.getTrialEndsAt())
                .currentPeriodEnd(source.getCurrentPeriodEnd())
                .suspended(source.isSuspended())
                .notes(source.getNotes())
                .build();
    }
}
