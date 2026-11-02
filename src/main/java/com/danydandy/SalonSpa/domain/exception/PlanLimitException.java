package com.danydandy.SalonSpa.domain.exception;

import org.springframework.http.HttpStatus;

/** Adding one more branch / user would pass the limit of the salon's plan. */
public class PlanLimitException extends ApiException {

    public static final String CODE = "PLAN_LIMIT_REACHED";

    public PlanLimitException(String message) {
        super(HttpStatus.FORBIDDEN, CODE, message);
    }
}
