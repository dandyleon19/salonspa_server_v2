package com.danydandy.SalonSpa.infrastructure.exception;

import com.danydandy.SalonSpa.application.dto.request.LoginRequest;
import com.danydandy.SalonSpa.domain.exception.NotFoundException;
import com.danydandy.SalonSpa.domain.exception.UnauthorizedException;
import jakarta.validation.Valid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

class GlobalExceptionHandlerTest {

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient
                .bindToController(new TestController())
                .controllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldReturnNotFoundWithStructuredBody() {
        webTestClient.get()
                .uri("/test/not-found")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.code").isEqualTo("NOT_FOUND")
                .jsonPath("$.message").isEqualTo("Client not found with id: 99")
                .jsonPath("$.path").isEqualTo("/test/not-found");
    }

    @Test
    void shouldReturnValidationErrorForInvalidRequestBody() {
        webTestClient.post()
                .uri("/test/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"email\":\"\",\"password\":\"\"}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.code").isEqualTo("VALIDATION_ERROR")
                .jsonPath("$.message").value(message -> {
                    String text = message.toString();
                    assertTrue(text.contains("email"));
                    assertTrue(text.contains("password"));
                });
    }

    @Test
    void shouldReturnUnauthorizedWithStructuredBody() {
        webTestClient.get()
                .uri("/test/unauthorized")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.status").isEqualTo(401)
                .jsonPath("$.code").isEqualTo("UNAUTHORIZED")
                .jsonPath("$.message").isEqualTo("Invalid email or password");
    }

    @RestController
    static class TestController {

        @GetMapping("/test/not-found")
        Mono<Void> notFound() {
            return Mono.error(NotFoundException.forResource("Client", 99));
        }

        @GetMapping("/test/unauthorized")
        Mono<Void> unauthorized() {
            return Mono.error(new UnauthorizedException("Invalid email or password"));
        }

        @PostMapping("/test/login")
        Mono<Void> login(@Valid @RequestBody LoginRequest request) {
            return Mono.empty();
        }
    }
}
