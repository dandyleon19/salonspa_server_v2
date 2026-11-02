package com.danydandy.SalonSpa.application.dto.response;

import com.danydandy.SalonSpa.domain.model.Feature;
import com.danydandy.SalonSpa.domain.model.Plan;
import com.danydandy.SalonSpa.domain.model.SalonSubscription;
import com.danydandy.SalonSpa.domain.model.SubscriptionStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A salon subscription as the frontend needs it: state, dates, and what the plan allows (features and limits).
 * maxBranches / maxUsers null means unlimited.
 */
public record SubscriptionResponse(
        Long salonId,
        String salonName,
        String planCode,
        String planName,
        String status,
        boolean blocked,
        /** Blocked by hand; {@code resumeStatus} is where it goes back to when it is reactivated. */
        boolean suspended,
        String resumeStatus,
        /** True while the salon is on its free trial (even if the trial already ended or the salon is suspended). */
        boolean onTrial,
        /** Length of this salon's trial in days. */
        Integer trialDays,
        /** True while the trial days have not started counting (the salon has not been opened yet). */
        boolean trialPending,
        LocalDateTime trialStartedAt,
        LocalDateTime trialEndsAt,
        LocalDateTime currentPeriodEnd,
        LocalDateTime graceEndsAt,
        Integer daysRemaining,
        Integer maxBranches,
        Integer maxUsers,
        List<String> features,
        String notes
) {
    public static SubscriptionResponse from(SalonSubscription subscription) {
        Plan plan = subscription.getPlan();
        return new SubscriptionResponse(
                subscription.getSalonId(),
                subscription.getSalonName(),
                plan == null ? null : plan.getCode(),
                plan == null ? null : plan.getName(),
                subscription.getStatus() == null ? null : subscription.getStatus().name(),
                subscription.isBlocked(),
                subscription.isSuspended(),
                subscription.getResumeStatus() == null ? null : subscription.getResumeStatus().name(),
                subscription.getStoredStatus() == SubscriptionStatus.TRIAL,
                subscription.getTrialDays(),
                subscription.isTrialPending(),
                subscription.getTrialStartedAt(),
                subscription.getTrialEndsAt(),
                subscription.getCurrentPeriodEnd(),
                subscription.getGraceEndsAt(),
                subscription.getDaysRemaining(),
                plan == null ? null : plan.getMaxBranches(),
                plan == null ? null : plan.getMaxUsers(),
                plan == null || plan.getFeatures() == null ? List.of() : plan.getFeatures().stream().sorted().toList(),
                subscription.getNotes()
        );
    }

    /** SUPER_ADMIN is never limited by a plan: everything is available. */
    public static SubscriptionResponse forSuperAdmin() {
        return new SubscriptionResponse(null, null, null, null, "ACTIVE", false, false, null, false, null, false, null, null, null,
                null, null, null, null, Feature.ALL, null);
    }
}
