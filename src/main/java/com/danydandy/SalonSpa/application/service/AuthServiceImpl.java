package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.application.dto.response.AuthResponse;
import com.danydandy.SalonSpa.domain.exception.ConflictException;
import com.danydandy.SalonSpa.domain.exception.UnauthorizedException;
import com.danydandy.SalonSpa.domain.model.AuthUser;
import com.danydandy.SalonSpa.domain.model.Role;
import com.danydandy.SalonSpa.domain.model.Salon;
import com.danydandy.SalonSpa.domain.model.User;
import com.danydandy.SalonSpa.domain.ports.in.AuthUseCase;
import com.danydandy.SalonSpa.domain.ports.out.SalonRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.UserRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AuthServiceImpl implements AuthUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final SalonRepositoryPort salonRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public Mono<User> register(User user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        user.setIsActive(true);
        if (user.getRole() == null) {
            user.setRole(Role.STAFF_USER);
        }
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (AuthUser) ctx.getAuthentication().getPrincipal())
                .switchIfEmpty(Mono.error(new UnauthorizedException("Authentication required")))
                .flatMap(authUser -> {
                    user.setSalonId(authUser.getSalonId());
                    return userRepositoryPort.save(user);
                });
    }

    @Override
    public Mono<AuthResponse> login(String email, String password) {
        return userRepositoryPort.findByEmail(email)
                .switchIfEmpty(Mono.error(new UnauthorizedException("Invalid email or password")))
                .flatMap(user -> {
                    boolean matches = passwordEncoder.matches(password, user.getPassword());
                    if (!matches) {
                        return Mono.error(new UnauthorizedException("Invalid email or password"));
                    }

                    return Mono.just(buildAuthResponse(user));
                });
    }

    @Override
    public Mono<AuthResponse> bootstrap(User admin, Salon salon) {
        return userRepositoryPort.countAll(null, null, null)
                .flatMap(userCount -> {
                    if (userCount > 0) {
                        return Mono.error(new ConflictException("System already initialized"));
                    }
                    return salonRepositoryPort.save(salon)
                            .flatMap(savedSalon -> {
                                admin.setPassword(passwordEncoder.encode(admin.getPassword()));
                                admin.setIsActive(true);
                                admin.setRole(Role.SUPER_ADMIN);
                                admin.setSalonId(savedSalon.getId());
                                return userRepositoryPort.save(admin)
                                        .map(this::buildAuthResponse);
                            });
                });
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtService.generateToken(user.getId(), user.getSalonId(), user.getRole());
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getSalonId(), user.getRole());
    }
}
