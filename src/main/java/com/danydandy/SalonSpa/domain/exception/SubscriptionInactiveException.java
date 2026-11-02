package com.danydandy.SalonSpa.domain.exception;

import org.springframework.http.HttpStatus;

/** The salon has no usable subscription (trial over, unpaid, or suspended). */
public class SubscriptionInactiveException extends ApiException {

    public static final String CODE = "SUBSCRIPTION_INACTIVE";

    public SubscriptionInactiveException(String message) {
        super(HttpStatus.PAYMENT_REQUIRED, CODE, message);
    }
}
