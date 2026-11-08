package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.application.security.SecurityHelper;
import com.danydandy.SalonSpa.application.util.SearchHelper;
import com.danydandy.SalonSpa.domain.exception.ForbiddenException;
import com.danydandy.SalonSpa.domain.exception.NotFoundException;
import com.danydandy.SalonSpa.domain.model.Role;
import com.danydandy.SalonSpa.domain.model.User;
import com.danydandy.SalonSpa.domain.ports.in.UserUseCase;
import com.danydandy.SalonSpa.domain.ports.out.RefreshTokenRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.SalonRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserService implements UserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final SalonRepositoryPort salonRepositoryPort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<PageResponse<User>> findPage(int page, int size, Boolean isActive, Role role, String search) {
        String roleFilter = role != null ? role.name() : null;
        String searchFilter = SearchHelper.toLikePattern(search);
        return SecurityHelper.currentUser()
                .flatMap(authUser -> {
                    if (SecurityHelper.isSuperAdmin(authUser)) {
                        return paginateAll(page, size, isActive, roleFilter, searchFilter);
                    }
                    return paginateBySalonId(authUser.getSalonId(), page, size, isActive, roleFilter, searchFilter);
                });
    }

    @Override
    public Mono<User> findById(Long id) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> {
                    if (SecurityHelper.isStaffUser(authUser) && !id.equals(authUser.getUserId())) {
                        return Mono.error(NotFoundException.forResource("User", id));
                    }
                    return userRepositoryPort.findById(id)
                            .switchIfEmpty(Mono.error(NotFoundException.forResource("User", id)))
                            .flatMap(user -> SecurityHelper.requireSameSalon(user, user.getSalonId(), authUser, "User", id))
                            .flatMap(this::enrichWithSalon);
                });
    }

    @Override
    public Mono<User> update(Long id, User user) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> userRepositoryPort.findById(id)
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("User", id)))
                        .flatMap(existing -> SecurityHelper.requireSameSalon(existing, existing.getSalonId(), authUser, "User", id))
                        .flatMap(existing -> {
                            existing.setFirstName(user.getFirstName());
                            existing.setLastName(user.getLastName());
                            existing.setEmail(user.getEmail());
                            existing.setIsActive(user.getIsActive());
                            existing.setCommissionPercentage(user.getCommissionPercentage());
                            existing.setRole(user.getRole());
                            return userRepositoryPort.save(existing);
                        }));
    }

    @Override
    public Mono<Void> resetPassword(Long id, String newPassword) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> userRepositoryPort.findById(id)
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("User", id)))
                        .flatMap(existing -> SecurityHelper.requireSameSalon(existing, existing.getSalonId(), authUser, "User", id))
                        .flatMap(existing -> {
                            // The platform owner's account is only changed by the platform owner.
                            if (existing.getRole() == Role.SUPER_ADMIN && !SecurityHelper.isSuperAdmin(authUser)) {
                                return Mono.<Void>error(new ForbiddenException(
                                        "Solo un super administrador puede cambiar la contraseña de un super administrador"));
                            }
                            existing.setPassword(passwordEncoder.encode(newPassword));
                            return userRepositoryPort.save(existing)
                                    // The old password may be in someone else's hands: every open session ends.
                                    .then(Mono.defer(() -> refreshTokenRepositoryPort.revokeAllByUserId(id)));
                        }));
    }

    @Override
    public Mono<Void> delete(Long id) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> userRepositoryPort.findById(id)
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("User", id)))
                        .flatMap(user -> SecurityHelper.requireSameSalon(user, user.getSalonId(), authUser, "User", id))
                        .flatMap(user -> userRepositoryPort.deleteById(id)));
    }

    private Mono<User> enrichWithSalon(User user) {
        if (user.getSalonId() == null) {
            return Mono.just(user);
        }
        return salonRepositoryPort.findById(user.getSalonId())
                .map(salon -> {
                    user.setSalon(salon);
                    return user;
                });
    }

    private Mono<PageResponse<User>> paginateAll(int page, int size, Boolean isActive, String role, String search) {
        return Mono.zip(
                userRepositoryPort.countAll(isActive, role, search),
                userRepositoryPort.findAll(page, size, isActive, role, search)
                        .flatMap(this::enrichWithSalon)
                        .collectList()
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }

    private Mono<PageResponse<User>> paginateBySalonId(Long salonId, int page, int size, Boolean isActive,
                                                         String role, String search) {
        return Mono.zip(
                userRepositoryPort.countBySalonId(salonId, isActive, role, search),
                userRepositoryPort.findBySalonId(salonId, page, size, isActive, role, search)
                        .flatMap(this::enrichWithSalon)
                        .collectList()
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }
}
