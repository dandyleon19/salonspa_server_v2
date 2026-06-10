package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.application.dto.request.BootstrapRequest;
import com.danydandy.SalonSpa.application.dto.request.CreateUserRequest;
import com.danydandy.SalonSpa.application.dto.request.LoginRequest;
import com.danydandy.SalonSpa.application.mapper.RequestDtoMapper;
import com.danydandy.SalonSpa.domain.model.AuthResponse;
import com.danydandy.SalonSpa.domain.model.User;
import com.danydandy.SalonSpa.domain.ports.in.AuthUseCase;
import jakarta.validation.Valid;
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
    private final RequestDtoMapper requestDtoMapper;

    @PostMapping("/register")
    public Mono<ResponseEntity<User>> register(@Valid @RequestBody CreateUserRequest request) {
        return authUseCase.register(requestDtoMapper.toUser(request))
                .map(user -> ResponseEntity.status(HttpStatus.CREATED).body(user));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return authUseCase.login(request.getEmail(), request.getPassword())
                .map(ResponseEntity::ok);
    }

    @PostMapping("/bootstrap")
    public Mono<ResponseEntity<AuthResponse>> bootstrap(@Valid @RequestBody BootstrapRequest request) {
        return authUseCase.bootstrap(
                        requestDtoMapper.toUser(request.getAdmin()),
                        requestDtoMapper.toSalon(request.getSalon())
                )
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }
}
