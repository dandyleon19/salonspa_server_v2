package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.domain.exception.BadRequestException;
import com.danydandy.SalonSpa.domain.exception.ConflictException;
import com.danydandy.SalonSpa.domain.exception.NotFoundException;
import com.danydandy.SalonSpa.domain.exception.UnauthorizedException;
import com.danydandy.SalonSpa.domain.model.AuthUser;
import com.danydandy.SalonSpa.domain.model.Role;
import com.danydandy.SalonSpa.domain.model.Salon;
import com.danydandy.SalonSpa.domain.model.User;
import com.danydandy.SalonSpa.config.properties.JwtProperties;
import com.danydandy.SalonSpa.domain.model.RefreshToken;
import com.danydandy.SalonSpa.domain.ports.out.RefreshTokenRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.SalonRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.UserRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private SalonRepositoryPort salonRepositoryPort;

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private JwtProperties jwtProperties;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(
                userRepositoryPort,
                salonRepositoryPort,
                refreshTokenRepositoryPort,
                passwordEncoder,
                jwtService,
                jwtProperties
        );
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
                .role(Role.SUPER_ADMIN)
                .isActive(true)
                .build();

        when(userRepositoryPort.countAll(null, null, null)).thenReturn(Mono.just(0L));
        when(salonRepositoryPort.save(salon)).thenReturn(Mono.just(savedSalon));
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepositoryPort.save(any(User.class))).thenReturn(Mono.just(savedAdmin));
        when(jwtService.generateToken(10L, null, Role.SUPER_ADMIN)).thenReturn("jwt-token");
        when(jwtProperties.refreshExpirationMs()).thenReturn(604800000L);
        when(refreshTokenRepositoryPort.save(any(RefreshToken.class))).thenAnswer(invocation -> {
            RefreshToken token = invocation.getArgument(0);
            token.setId(1L);
            return Mono.just(token);
        });

        StepVerifier.create(authService.bootstrap(admin, salon))
                .expectNextMatches(response ->
                        response.token().equals("jwt-token")
                                && response.refreshToken() != null
                                && !response.refreshToken().isBlank()
                                && response.userId().equals(10L)
                                && response.salonId() == null
                                && response.role() == Role.SUPER_ADMIN
                )
                .verifyComplete();

        verify(userRepositoryPort).save(any(User.class));
    }

    @Test
    void shouldRejectBootstrapWhenUsersAlreadyExist() {
        when(userRepositoryPort.countAll(null, null, null)).thenReturn(Mono.just(1L));

        StepVerifier.create(authService.bootstrap(
                        User.builder().email("admin@spa.com").password("password123").build(),
                        Salon.builder().name("Spa Central").build()
                ))
                .expectError(ConflictException.class)
                .verify();
    }

    @Test
    void shouldRegisterUserWithCreatorSalonWhenAdmin() {
        AuthUser admin = new AuthUser(1L, 5L, Role.ADMIN_USER.name());
        User user = User.builder()
                .firstName("Staff")
                .lastName("User")
                .email("staff@spa.com")
                .password("password123")
                .salonId(99L)
                .role(Role.STAFF_USER)
                .build();
        User savedUser = User.builder()
                .id(20L)
                .firstName("Staff")
                .lastName("User")
                .email("staff@spa.com")
                .password("hashed")
                .salonId(5L)
                .role(Role.STAFF_USER)
                .isActive(true)
                .build();

        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepositoryPort.save(any(User.class))).thenReturn(Mono.just(savedUser));

        StepVerifier.create(
                        authService.register(user)
                                .contextWrite(securityContext(admin))
                )
                .expectNextMatches(saved -> saved.getSalonId().equals(5L))
                .verifyComplete();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepositoryPort).save(captor.capture());
        assertThat(captor.getValue().getSalonId()).isEqualTo(5L);
    }

    @Test
    void shouldRegisterUserWithPayloadSalonWhenSuperAdmin() {
        AuthUser superAdmin = new AuthUser(1L, null, Role.SUPER_ADMIN.name());
        User user = User.builder()
                .firstName("Staff")
                .lastName("User")
                .email("staff@spa.com")
                .password("password123")
                .salonId(3L)
                .role(Role.STAFF_USER)
                .build();
        User savedUser = User.builder()
                .id(20L)
                .firstName("Staff")
                .lastName("User")
                .email("staff@spa.com")
                .password("hashed")
                .salonId(3L)
                .role(Role.STAFF_USER)
                .isActive(true)
                .build();

        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(salonRepositoryPort.findById(3L)).thenReturn(Mono.just(Salon.builder().id(3L).name("Spa Norte").build()));
        when(userRepositoryPort.save(any(User.class))).thenReturn(Mono.just(savedUser));

        StepVerifier.create(
                        authService.register(user)
                                .contextWrite(securityContext(superAdmin))
                )
                .expectNextMatches(saved -> saved.getSalonId().equals(3L))
                .verifyComplete();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepositoryPort).save(captor.capture());
        assertThat(captor.getValue().getSalonId()).isEqualTo(3L);
    }

    @Test
    void shouldRejectRegisterWithoutSalonWhenSuperAdmin() {
        AuthUser superAdmin = new AuthUser(1L, null, Role.SUPER_ADMIN.name());
        User user = User.builder()
                .firstName("Staff")
                .lastName("User")
                .email("staff@spa.com")
                .password("password123")
                .role(Role.STAFF_USER)
                .build();

        when(passwordEncoder.encode("password123")).thenReturn("hashed");

        StepVerifier.create(
                        authService.register(user)
                                .contextWrite(securityContext(superAdmin))
                )
                .expectError(BadRequestException.class)
                .verify();
    }

    @Test
    void shouldRejectRegisterWithUnknownSalonWhenSuperAdmin() {
        AuthUser superAdmin = new AuthUser(1L, null, Role.SUPER_ADMIN.name());
        User user = User.builder()
                .firstName("Staff")
                .lastName("User")
                .email("staff@spa.com")
                .password("password123")
                .salonId(999L)
                .role(Role.STAFF_USER)
                .build();

        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(salonRepositoryPort.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(
                        authService.register(user)
                                .contextWrite(securityContext(superAdmin))
                )
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void shouldRefreshTokensAndRotateStoredRefreshToken() {
        User user = User.builder()
                .id(10L)
                .email("admin@spa.com")
                .salonId(5L)
                .role(Role.ADMIN_USER)
                .isActive(true)
                .build();
        RefreshToken storedToken = RefreshToken.builder()
                .id(99L)
                .userId(10L)
                .tokenHash("hash")
                .build();

        when(refreshTokenRepositoryPort.findValidByTokenHash(anyString())).thenReturn(Mono.just(storedToken));
        when(userRepositoryPort.findById(10L)).thenReturn(Mono.just(user));
        when(refreshTokenRepositoryPort.revokeById(99L)).thenReturn(Mono.empty());
        when(jwtProperties.refreshExpirationMs()).thenReturn(604800000L);
        when(jwtService.generateToken(10L, 5L, Role.ADMIN_USER)).thenReturn("new-access-token");
        when(refreshTokenRepositoryPort.save(any(RefreshToken.class))).thenAnswer(invocation -> {
            RefreshToken token = invocation.getArgument(0);
            token.setId(100L);
            return Mono.just(token);
        });

        StepVerifier.create(authService.refresh("raw-refresh-token"))
                .expectNextMatches(response ->
                        response.token().equals("new-access-token")
                                && response.refreshToken() != null
                                && !response.refreshToken().isBlank()
                                && response.userId().equals(10L)
                )
                .verifyComplete();

        verify(refreshTokenRepositoryPort).revokeById(99L);
    }

    @Test
    void shouldRejectRefreshWithInvalidToken() {
        when(refreshTokenRepositoryPort.findValidByTokenHash(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(authService.refresh("invalid-token"))
                .expectError(UnauthorizedException.class)
                .verify();

        verify(refreshTokenRepositoryPort, never()).revokeById(anyLong());
    }

    @Test
    void shouldLogoutByRevokingRefreshToken() {
        RefreshToken storedToken = RefreshToken.builder()
                .id(99L)
                .userId(10L)
                .tokenHash("hash")
                .build();

        when(refreshTokenRepositoryPort.findValidByTokenHash(anyString())).thenReturn(Mono.just(storedToken));
        when(refreshTokenRepositoryPort.revokeById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(authService.logout("raw-refresh-token"))
                .verifyComplete();

        verify(refreshTokenRepositoryPort).revokeById(99L);
    }

    private static reactor.util.context.Context securityContext(AuthUser authUser) {
        SecurityContext context = new SecurityContextImpl(
                new UsernamePasswordAuthenticationToken(authUser, null)
        );
        return ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context));
    }
}
