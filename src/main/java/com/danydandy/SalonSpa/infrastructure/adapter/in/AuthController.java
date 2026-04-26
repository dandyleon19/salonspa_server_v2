package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.domain.model.AuthResponse;
import com.danydandy.SalonSpa.domain.model.LoginRequest;
import com.danydandy.SalonSpa.domain.model.User;
import com.danydandy.SalonSpa.domain.ports.in.AuthUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/register")
    public ResponseEntity<Mono<User>> register(@RequestBody User user) {
        return new ResponseEntity<>(authUseCase.register(user), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Mono<AuthResponse>> login(@RequestBody LoginRequest request) {
        return new ResponseEntity<>(authUseCase.login(request.getEmail(), request.getPassword()), HttpStatus.OK);
    }
}
