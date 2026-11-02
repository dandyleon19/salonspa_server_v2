package com.danydandy.SalonSpa.domain.exception;

import org.springframework.http.HttpStatus;

/** The salon's plan does not include the requested feature. */
public class PlanRequiredException extends ApiException {

    public static final String CODE = "PLAN_REQUIRED";

    public PlanRequiredException(String message) {
        super(HttpStatus.FORBIDDEN, CODE, message);
    }
}
