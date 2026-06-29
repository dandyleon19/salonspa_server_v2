package com.danydandy.SalonSpa.domain.ports.in;

import com.danydandy.SalonSpa.application.dto.response.AuthResponse;
import com.danydandy.SalonSpa.domain.model.Salon;
import com.danydandy.SalonSpa.domain.model.User;
import reactor.core.publisher.Mono;

public interface AuthUseCase {
    Mono<User> register(User user);
    Mono<AuthResponse> login(String email, String password);
    Mono<AuthResponse> bootstrap(User admin, Salon salon);
    Mono<AuthResponse> refresh(String refreshToken);
    Mono<Void> logout(String refreshToken);
}
