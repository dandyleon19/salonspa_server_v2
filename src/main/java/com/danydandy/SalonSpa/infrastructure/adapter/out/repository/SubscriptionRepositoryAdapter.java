package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.domain.model.Plan;
import com.danydandy.SalonSpa.domain.model.SalonSubscription;
import com.danydandy.SalonSpa.domain.model.SubscriptionEvent;
import com.danydandy.SalonSpa.domain.model.SubscriptionStatus;
import com.danydandy.SalonSpa.domain.ports.out.SubscriptionRepositoryPort;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Hand-written SQL (no Spring Data entities / MapStruct): the subscription tables are small and every read joins a few
 * of them, so plain rows are clearer than three entities and their mappers.
 */
@RequiredArgsConstructor
public class SubscriptionRepositoryAdapter implements SubscriptionRepositoryPort {

    /** Same default as the column; only used when a subscription is saved without a trial length. */
    private static final int DEFAULT_TRIAL_DAYS = 14;

    private final DatabaseClient databaseClient;

    /** Columns of salon_subscriptions plus, for listings, the salon name. */
    private record SubscriptionRow(
            Long salonId,
            String salonName,
            Long planId,
            String status,
            Integer trialDays,
            LocalDateTime trialStartedAt,
            LocalDateTime trialEndsAt,
            LocalDateTime currentPeriodEnd,
            Boolean suspended,
            String notes
    ) {
    }

    // ------------------------------------------------------------------ plans

    @Override
    public Flux<Plan> findPlans() {
        Mono<Map<Long, Set<String>>> featuresByPlan = databaseClient
                .sql("SELECT plan_id, feature_key FROM plan_features")
                .map((row, metadata) -> Map.entry(row.get("plan_id", Long.class), row.get("feature_key", String.class)))
                .all()
                .collect(HashMap::new, (map, entry) ->
                        map.computeIfAbsent(entry.getKey(), key -> new HashSet<>()).add(entry.getValue()));

        return databaseClient
                .sql("""
                        SELECT id, code, name, price_monthly, max_branches, max_users, sort_order
                        FROM plans
                        WHERE is_active = TRUE
                        ORDER BY sort_order, id
                        """)
                .map((row, metadata) -> toPlan(row))
                .all()
                .collectList()
                .zipWith(featuresByPlan)
                .flatMapMany(tuple -> Flux.fromIterable(tuple.getT1()).map(plan -> {
                    plan.setFeatures(tuple.getT2().getOrDefault(plan.getId(), Set.of()));
                    return plan;
                }));
    }

    @Override
    public Mono<Plan> findPlanByCode(String code) {
        return findPlans()
                .filter(plan -> plan.getCode().equalsIgnoreCase(code))
                .next();
    }

    // ------------------------------------------------------------------ subscriptions

    @Override
    public Mono<SalonSubscription> findBySalonId(Long salonId) {
        return databaseClient
                .sql("""
                        SELECT salon_id, CAST(NULL AS VARCHAR) AS salon_name, plan_id, status, trial_days,
                               trial_started_at, trial_ends_at, current_period_end, suspended, notes
                        FROM salon_subscriptions
                        WHERE salon_id = :salonId
                        """)
                .bind("salonId", salonId)
                .map((row, metadata) -> toSubscriptionRow(row))
                .one()
                .zipWith(plansById())
                .map(tuple -> toSubscription(tuple.getT1(), tuple.getT2()));
    }

    @Override
    public Flux<SalonSubscription> findAllWithSalonName() {
        return databaseClient
                .sql("""
                        SELECT sa.id AS salon_id, sa.name AS salon_name, s.plan_id, s.status, s.trial_days,
                               s.trial_started_at, s.trial_ends_at, s.current_period_end, s.suspended, s.notes
                        FROM salons sa
                                 LEFT JOIN salon_subscriptions s ON s.salon_id = sa.id
                        ORDER BY sa.name, sa.id
                        """)
                .map((row, metadata) -> toSubscriptionRow(row))
                .all()
                .collectList()
                .zipWith(plansById())
                .flatMapMany(tuple -> Flux.fromIterable(tuple.getT1())
                        .map(row -> toSubscription(row, tuple.getT2())));
    }

    @Override
    public Mono<Void> save(SalonSubscription subscription) {
        DatabaseClient.GenericExecuteSpec spec = databaseClient
                .sql("""
                        INSERT INTO salon_subscriptions
                            (salon_id, plan_id, status, trial_days, trial_started_at, trial_ends_at,
                             current_period_end, suspended, notes)
                        VALUES (:salonId, :planId, :status, :trialDays, :trialStartedAt, :trialEndsAt,
                                :currentPeriodEnd, :suspended, :notes)
                        ON CONFLICT (salon_id) DO UPDATE
                            SET plan_id            = EXCLUDED.plan_id,
                                status             = EXCLUDED.status,
                                trial_days         = EXCLUDED.trial_days,
                                trial_started_at   = EXCLUDED.trial_started_at,
                                trial_ends_at      = EXCLUDED.trial_ends_at,
                                current_period_end = EXCLUDED.current_period_end,
                                suspended          = EXCLUDED.suspended,
                                notes              = EXCLUDED.notes,
                                updated_at         = CURRENT_TIMESTAMP
                        """)
                .bind("salonId", subscription.getSalonId())
                .bind("planId", subscription.getPlan().getId())
                .bind("status", subscription.getStoredStatus().name())
                .bind("trialDays", subscription.getTrialDays() != null ? subscription.getTrialDays() : DEFAULT_TRIAL_DAYS)
                .bind("suspended", subscription.isSuspended());
        spec = bindOrNull(spec, "trialStartedAt", subscription.getTrialStartedAt(), LocalDateTime.class);
        spec = bindOrNull(spec, "trialEndsAt", subscription.getTrialEndsAt(), LocalDateTime.class);
        spec = bindOrNull(spec, "currentPeriodEnd", subscription.getCurrentPeriodEnd(), LocalDateTime.class);
        spec = bindOrNull(spec, "notes", subscription.getNotes(), String.class);
        return spec.fetch().rowsUpdated().then();
    }

    // ------------------------------------------------------------------ events

    @Override
    public Mono<Void> saveEvent(SubscriptionEvent event) {
        DatabaseClient.GenericExecuteSpec spec = databaseClient
                .sql("""
                        INSERT INTO subscription_events (salon_id, event_type, detail, user_id)
                        VALUES (:salonId, :eventType, :detail, :userId)
                        """)
                .bind("salonId", event.getSalonId())
                .bind("eventType", event.getEventType());
        spec = bindOrNull(spec, "detail", event.getDetail(), String.class);
        spec = bindOrNull(spec, "userId", event.getUserId(), Long.class);
        return spec.fetch().rowsUpdated().then();
    }

    @Override
    public Flux<SubscriptionEvent> findEvents(Long salonId, int limit) {
        return databaseClient
                .sql("""
                        SELECT id, salon_id, event_type, detail, user_id, created_at
                        FROM subscription_events
                        WHERE salon_id = :salonId
                        ORDER BY created_at DESC, id DESC
                        LIMIT :limit
                        """)
                .bind("salonId", salonId)
                .bind("limit", limit)
                .map((row, metadata) -> SubscriptionEvent.builder()
                        .id(row.get("id", Long.class))
                        .salonId(row.get("salon_id", Long.class))
                        .eventType(row.get("event_type", String.class))
                        .detail(row.get("detail", String.class))
                        .userId(row.get("user_id", Long.class))
                        .createdAt(row.get("created_at", LocalDateTime.class))
                        .build())
                .all();
    }

    // ------------------------------------------------------------------ helpers

    private Mono<Map<Long, Plan>> plansById() {
        return findPlans().collectMap(Plan::getId);
    }

    private static Plan toPlan(Row row) {
        return Plan.builder()
                .id(row.get("id", Long.class))
                .code(row.get("code", String.class))
                .name(row.get("name", String.class))
                .priceMonthly(row.get("price_monthly", BigDecimal.class))
                .maxBranches(row.get("max_branches", Integer.class))
                .maxUsers(row.get("max_users", Integer.class))
                .sortOrder(row.get("sort_order", Integer.class))
                .features(Set.of())
                .build();
    }

    private static SubscriptionRow toSubscriptionRow(Row row) {
        return new SubscriptionRow(
                row.get("salon_id", Long.class),
                row.get("salon_name", String.class),
                row.get("plan_id", Long.class),
                row.get("status", String.class),
                row.get("trial_days", Integer.class),
                row.get("trial_started_at", LocalDateTime.class),
                row.get("trial_ends_at", LocalDateTime.class),
                row.get("current_period_end", LocalDateTime.class),
                row.get("suspended", Boolean.class),
                row.get("notes", String.class)
        );
    }

    private static SalonSubscription toSubscription(SubscriptionRow row, Map<Long, Plan> plans) {
        return SalonSubscription.builder()
                .salonId(row.salonId())
                .salonName(row.salonName())
                .plan(row.planId() == null ? null : plans.get(row.planId()))
                .storedStatus(row.status() == null ? null : SubscriptionStatus.valueOf(row.status()))
                .trialDays(row.trialDays())
                .trialStartedAt(row.trialStartedAt())
                .trialEndsAt(row.trialEndsAt())
                .currentPeriodEnd(row.currentPeriodEnd())
                .suspended(Boolean.TRUE.equals(row.suspended()))
                .notes(row.notes())
                .build();
    }

    private static <T> DatabaseClient.GenericExecuteSpec bindOrNull(
            DatabaseClient.GenericExecuteSpec spec,
            String name,
            T value,
            Class<T> type
    ) {
        return value == null ? spec.bindNull(name, type) : spec.bind(name, value);
    }
}
