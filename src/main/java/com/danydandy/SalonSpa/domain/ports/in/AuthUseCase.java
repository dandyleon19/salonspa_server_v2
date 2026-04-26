package com.danydandy.SalonSpa.domain.ports.in;

import com.danydandy.SalonSpa.domain.model.AuthResponse;
import com.danydandy.SalonSpa.domain.model.User;
import reactor.core.publisher.Mono;

public interface AuthUseCase {
    Mono<User> register(User user);
    Mono<AuthResponse> login(String email, String password);
}
