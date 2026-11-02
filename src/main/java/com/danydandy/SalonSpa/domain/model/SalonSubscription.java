package com.danydandy.SalonSpa.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalonSubscription {
    private Long salonId;
    private String salonName;
    private Plan plan;
    /** Stored state: TRIAL, ACTIVE or SUSPENDED. */
    private SubscriptionStatus storedStatus;
    /** Length of this salon's trial. Changeable per salon; a new salon gets the default. */
    private Integer trialDays;
    /** When the first user of the salon opened the app; null until then. */
    private LocalDateTime trialStartedAt;
    private LocalDateTime trialEndsAt;
    private LocalDateTime currentPeriodEnd;
    /** Blocked by hand. Independent of the state above, so reactivating goes back to exactly what it was. */
    private boolean suspended;
    private String notes;

    // Derived by SubscriptionServiceImpl each time the subscription is read.
    private SubscriptionStatus status;
    private LocalDateTime graceEndsAt;
    /** Whole days until the trial / period / grace ends; 0 when it already ended or never ends. */
    private Integer daysRemaining;
    /** While suspended: the state the salon returns to when it is reactivated. Null otherwise. */
    private SubscriptionStatus resumeStatus;

    /** A trial whose days have not started counting yet: they start the first time a user of the salon opens the app. */
    public boolean isTrialPending() {
        return storedStatus == SubscriptionStatus.TRIAL && trialEndsAt == null;
    }

    public boolean isBlocked() {
        return status != null && status.isBlocked();
    }

    public boolean hasFeature(String featureKey) {
        return plan != null && plan.hasFeature(featureKey);
    }
}
