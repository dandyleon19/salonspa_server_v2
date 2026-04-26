package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.domain.model.AuthResponse;
import com.danydandy.SalonSpa.domain.model.AuthUser;
import com.danydandy.SalonSpa.domain.model.User;
import com.danydandy.SalonSpa.domain.ports.in.AuthUseCase;
import com.danydandy.SalonSpa.domain.ports.out.UserRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public Mono<User> register(User user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        user.setIsActive(true);
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (AuthUser) ctx.getAuthentication().getPrincipal())
                .flatMap(authUser -> {
                    user.setSalonId(authUser.getSalonId());
                    return userRepositoryPort.save(user);
                });
    }

    @Override
    public Mono<AuthResponse> login(String email, String password) {
        return userRepositoryPort.findByEmail(email)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found.")))
                .flatMap(user -> {
                    boolean matches = passwordEncoder.matches(password, user.getPassword());
                    if (!matches) return Mono.error(new RuntimeException("Invalid Credentials."));

                    String token = jwtService.generateToken(user.getId(), user.getSalonId());

                    return Mono.just(
                            new AuthResponse(token, user.getId(), user.getEmail(), user.getSalonId())
                    );
                });
    }
}
