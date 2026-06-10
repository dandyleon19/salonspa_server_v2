package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.domain.exception.ConflictException;
import com.danydandy.SalonSpa.domain.model.Role;
import com.danydandy.SalonSpa.domain.model.Salon;
import com.danydandy.SalonSpa.domain.model.User;
import com.danydandy.SalonSpa.domain.ports.out.SalonRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.UserRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private SalonRepositoryPort salonRepositoryPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userRepositoryPort, salonRepositoryPort, passwordEncoder, jwtService);
    }

    @Test
    void shouldBootstrapFirstSuperAdminAndSalon() {
        Salon salon = Salon.builder().name("Spa Central").build();
        Salon savedSalon = Salon.builder().id(1L).name("Spa Central").build();
        User admin = User.builder()
                .firstName("Admin")
                .lastName("User")
                .email("admin@spa.com")
                .password("password123")
                .build();
        User savedAdmin = User.builder()
                .id(10L)
                .firstName("Admin")
                .lastName("User")
                .email("admin@spa.com")
                .password("hashed")
                .salonId(1L)
                .role(Role.SUPER_ADMIN)
                .isActive(true)
                .build();

        when(userRepositoryPort.countAll()).thenReturn(Mono.just(0L));
        when(salonRepositoryPort.save(salon)).thenReturn(Mono.just(savedSalon));
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepositoryPort.save(any(User.class))).thenReturn(Mono.just(savedAdmin));
        when(jwtService.generateToken(10L, 1L, Role.SUPER_ADMIN)).thenReturn("jwt-token");

        StepVerifier.create(authService.bootstrap(admin, salon))
                .expectNextMatches(response ->
                        response.getToken().equals("jwt-token")
                                && response.getUserId().equals(10L)
                                && response.getSalonId().equals(1L)
                                && response.getRole() == Role.SUPER_ADMIN
                )
                .verifyComplete();

        verify(userRepositoryPort).save(any(User.class));
    }

    @Test
    void shouldRejectBootstrapWhenUsersAlreadyExist() {
        when(userRepositoryPort.countAll()).thenReturn(Mono.just(1L));

        StepVerifier.create(authService.bootstrap(
                        User.builder().email("admin@spa.com").password("password123").build(),
                        Salon.builder().name("Spa Central").build()
                ))
                .expectError(ConflictException.class)
                .verify();
    }
}
