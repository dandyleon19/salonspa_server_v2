package com.danydandy.SalonSpa.application.security;

import com.danydandy.SalonSpa.domain.exception.NotFoundException;
import com.danydandy.SalonSpa.domain.model.AuthUser;
import com.danydandy.SalonSpa.domain.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class SecurityHelperTest {

    @Test
    void shouldAllowSuperAdminToAccessAnySalon() {
        AuthUser superAdmin = new AuthUser(1L, 10L, Role.SUPER_ADMIN.name());
        StepVerifier.create(
                SecurityHelper.requireSameSalon("entity", 99L, superAdmin, "Client", 1L)
        ).expectNext("entity").verifyComplete();
    }

    @Test
    void shouldAllowSameSalonAccess() {
        AuthUser user = new AuthUser(2L, 10L, Role.STAFF_USER.name());
        StepVerifier.create(
                SecurityHelper.requireSameSalon("entity", 10L, user, "Client", 1L)
        ).expectNext("entity").verifyComplete();
    }

    @Test
    void shouldDenyCrossSalonAccessWithNotFound() {
        AuthUser user = new AuthUser(2L, 10L, Role.STAFF_USER.name());
        StepVerifier.create(
                SecurityHelper.requireSameSalon("entity", 99L, user, "Client", 1L)
        ).expectErrorSatisfies(error -> {
            assert error instanceof NotFoundException;
            assert error.getMessage().equals("Client not found with id: 1");
        }).verify();
    }

    @Test
    void shouldResolveCurrentUserFromContext() {
        AuthUser user = new AuthUser(3L, 5L, Role.ADMIN_USER.name());
        SecurityContext context = new SecurityContextImpl(
                new UsernamePasswordAuthenticationToken(user, null)
        );

        StepVerifier.create(
                SecurityHelper.currentUser()
                        .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)))
        ).expectNext(user).verifyComplete();
    }
}
