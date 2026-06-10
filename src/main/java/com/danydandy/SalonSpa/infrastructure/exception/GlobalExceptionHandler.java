package com.danydandy.SalonSpa.infrastructure.exception;

import com.danydandy.SalonSpa.application.dto.response.ErrorResponse;
import com.danydandy.SalonSpa.domain.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleApiException(ApiException ex, ServerWebExchange exchange) {
        log.warn("[{}] {} - {}", ex.getCode(), exchange.getRequest().getPath().value(), ex.getMessage());
        return Mono.just(buildResponse(ex.getStatus(), ex.getCode(), ex.getMessage(), exchange));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidation(WebExchangeBindException ex, ServerWebExchange exchange) {
        String message = ex.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("Validation failed on {}: {}", exchange.getRequest().getPath().value(), message);
        return Mono.just(buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message, exchange));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleMethodValidation(
            HandlerMethodValidationException ex,
            ServerWebExchange exchange
    ) {
        String message = ex.getAllValidationResults().stream()
                .flatMap(result -> result.getResolvableErrors().stream())
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("Method validation failed on {}: {}", exchange.getRequest().getPath().value(), message);
        return Mono.just(buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message, exchange));
    }

    @ExceptionHandler(AuthenticationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleAuthentication(AuthenticationException ex, ServerWebExchange exchange) {
        log.warn("Authentication failed on {}: {}", exchange.getRequest().getPath().value(), ex.getMessage());
        return Mono.just(buildResponse(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Authentication required", exchange));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleAccessDenied(AccessDeniedException ex, ServerWebExchange exchange) {
        log.warn("Access denied on {}: {}", exchange.getRequest().getPath().value(), ex.getMessage());
        return Mono.just(buildResponse(HttpStatus.FORBIDDEN, "FORBIDDEN", "Access denied", exchange));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDataIntegrity(DataIntegrityViolationException ex, ServerWebExchange exchange) {
        log.warn("Data integrity violation on {}: {}", exchange.getRequest().getPath().value(), ex.getMessage());
        return Mono.just(buildResponse(
                HttpStatus.CONFLICT,
                "CONFLICT",
                "The operation conflicts with existing data",
                exchange
        ));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneric(Exception ex, ServerWebExchange exchange) {
        log.error("Unhandled exception on {}", exchange.getRequest().getPath().value(), ex);
        return Mono.just(buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR",
                "An unexpected error occurred",
                exchange
        ));
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String code,
            String message,
            ServerWebExchange exchange
    ) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code(code)
                .message(message)
                .path(exchange.getRequest().getPath().value())
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
