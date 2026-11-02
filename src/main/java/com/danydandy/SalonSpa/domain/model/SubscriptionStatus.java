package com.danydandy.SalonSpa.domain.model;

/**
 * Effective state of a salon's subscription. Only TRIAL and ACTIVE are stored (plus a separate "suspended" flag);
 * PAST_DUE, EXPIRED and SUSPENDED are derived every time the subscription is read.
 */
public enum SubscriptionStatus {
    /** Free trial running. */
    TRIAL,
    /** Paid and up to date (or with no expiry). */
    ACTIVE,
    /** The paid period ended but the grace days are still running: works normally, with a warning. */
    PAST_DUE,
    /** Trial ended, or the paid period and its grace days ended, without a payment: blocked. */
    EXPIRED,
    /** Blocked by hand by a SUPER_ADMIN. */
    SUSPENDED;

    public boolean isBlocked() {
        return this == EXPIRED || this == SUSPENDED;
    }
}
