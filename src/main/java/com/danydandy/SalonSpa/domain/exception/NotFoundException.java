package com.danydandy.SalonSpa.domain.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ApiException {

    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "NOT_FOUND", message);
    }

    public static NotFoundException forResource(String resource, Object id) {
        return new NotFoundException(resource + " not found with id: " + id);
    }
}
