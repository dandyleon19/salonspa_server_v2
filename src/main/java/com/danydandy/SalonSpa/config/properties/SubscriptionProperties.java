package com.danydandy.SalonSpa.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Subscription rules. {@code trialPlanCode} is the plan a new salon tries during its trial.
 */
@ConfigurationProperties(prefix = "app.subscription")
public record SubscriptionProperties(
        @DefaultValue("14") int trialDays,
        @DefaultValue("5") int graceDays,
        @DefaultValue("PREMIUM") String trialPlanCode
) {
}
