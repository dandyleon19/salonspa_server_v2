package com.danydandy.SalonSpa.application.security;

import com.danydandy.SalonSpa.domain.exception.NotFoundException;
import com.danydandy.SalonSpa.domain.model.AuthUser;
import com.danydandy.SalonSpa.domain.model.Role;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Mono;

public final class SecurityHelper {

    private SecurityHelper() {
    }

    public static Mono<AuthUser> currentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (AuthUser) ctx.getAuthentication().getPrincipal());
    }

    public static boolean isSuperAdmin(AuthUser authUser) {
        return Role.SUPER_ADMIN.name().equals(authUser.getRole());
    }

    public static <T> Mono<T> requireSameSalon(T entity, Long entitySalonId, AuthUser authUser, String resource, Object id) {
        if (isSuperAdmin(authUser)) {
            return Mono.just(entity);
        }
        if (entitySalonId == null || !entitySalonId.equals(authUser.getSalonId())) {
            return Mono.error(NotFoundException.forResource(resource, id));
        }
        return Mono.just(entity);
    }

    public static Mono<Void> requireSalonAccess(Long salonId, AuthUser authUser, String resource, Object id) {
        if (isSuperAdmin(authUser)) {
            return Mono.empty();
        }
        if (salonId == null || !salonId.equals(authUser.getSalonId())) {
            return Mono.error(NotFoundException.forResource(resource, id));
        }
        return Mono.empty();
    }
}
