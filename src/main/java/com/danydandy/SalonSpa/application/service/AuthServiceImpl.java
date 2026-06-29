package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.application.dto.response.AuthResponse;
import com.danydandy.SalonSpa.application.security.SecurityHelper;
import com.danydandy.SalonSpa.config.properties.JwtProperties;
import com.danydandy.SalonSpa.domain.exception.BadRequestException;
import com.danydandy.SalonSpa.domain.exception.ConflictException;
import com.danydandy.SalonSpa.domain.exception.NotFoundException;
import com.danydandy.SalonSpa.domain.exception.UnauthorizedException;
import com.danydandy.SalonSpa.domain.model.AuthUser;
import com.danydandy.SalonSpa.domain.model.RefreshToken;
import com.danydandy.SalonSpa.domain.model.Role;
import com.danydandy.SalonSpa.domain.model.Salon;
import com.danydandy.SalonSpa.domain.model.User;
import com.danydandy.SalonSpa.domain.ports.in.AuthUseCase;
import com.danydandy.SalonSpa.domain.ports.out.RefreshTokenRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.SalonRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.UserRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.security.JwtService;
import com.danydandy.SalonSpa.infrastructure.security.RefreshTokenGenerator;
import com.danydandy.SalonSpa.infrastructure.security.RefreshTokenHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class AuthServiceImpl implements AuthUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final SalonRepositoryPort salonRepositoryPort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    @Override
    public Mono<User> register(User user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        user.setIsActive(true);
        if (user.getRole() == null) {
            user.setRole(Role.STAFF_USER);
        }
        return SecurityHelper.currentUser()
                .switchIfEmpty(Mono.error(new UnauthorizedException("Authentication required")))
                .flatMap(authUser -> validateRegisterRole(user.getRole())
                        .then(resolveSalonId(user, authUser)))
                .flatMap(userRepositoryPort::save);
    }

    private Mono<Void> validateRegisterRole(Role role) {
        if (role == Role.SUPER_ADMIN) {
            return Mono.error(new BadRequestException("Cannot register a SUPER_ADMIN user"));
        }
        return Mono.empty();
    }

    private Mono<User> resolveSalonId(User user, AuthUser authUser) {
        if (SecurityHelper.isSuperAdmin(authUser)) {
            if (user.getSalonId() == null) {
                return Mono.error(new BadRequestException("Salon id is required when registering users as SUPER_ADMIN"));
            }
            return salonRepositoryPort.findById(user.getSalonId())
                    .switchIfEmpty(Mono.error(NotFoundException.forResource("Salon", user.getSalonId())))
                    .thenReturn(user);
        }
        user.setSalonId(authUser.getSalonId());
        return Mono.just(user);
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

                    return issueAuthResponse(user);
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
                                return userRepositoryPort.save(admin)
                                        .flatMap(this::issueAuthResponse);
                            });
                });
    }

    @Override
    public Mono<AuthResponse> refresh(String refreshToken) {
        return findValidRefreshToken(refreshToken)
                .flatMap(stored -> userRepositoryPort.findById(stored.getUserId())
                        .filter(user -> Boolean.TRUE.equals(user.getIsActive()))
                        .switchIfEmpty(Mono.error(invalidRefreshToken()))
                        .flatMap(user -> refreshTokenRepositoryPort.revokeById(stored.getId())
                                .then(issueAuthResponse(user))));
    }

    @Override
    public Mono<Void> logout(String refreshToken) {
        return findValidRefreshToken(refreshToken)
                .flatMap(stored -> refreshTokenRepositoryPort.revokeById(stored.getId()))
                .then();
    }

    private Mono<RefreshToken> findValidRefreshToken(String refreshToken) {
        return refreshTokenRepositoryPort.findValidByTokenHash(RefreshTokenHasher.hash(refreshToken))
                .switchIfEmpty(Mono.error(invalidRefreshToken()));
    }

    private Mono<AuthResponse> issueAuthResponse(User user) {
        String accessToken = jwtService.generateToken(user.getId(), user.getSalonId(), user.getRole());
        String rawRefreshToken = RefreshTokenGenerator.generate();

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(user.getId())
                .tokenHash(RefreshTokenHasher.hash(rawRefreshToken))
                .expiresAt(LocalDateTime.now().plus(Duration.ofMillis(jwtProperties.refreshExpirationMs())))
                .build();

        return refreshTokenRepositoryPort.save(refreshToken)
                .map(saved -> new AuthResponse(
                        accessToken,
                        rawRefreshToken,
                        user.getId(),
                        user.getEmail(),
                        user.getSalonId(),
                        user.getRole()
                ));
    }

    private UnauthorizedException invalidRefreshToken() {
        return new UnauthorizedException("Invalid or expired refresh token");
    }
}
