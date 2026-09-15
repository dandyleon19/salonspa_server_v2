package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.application.security.SecurityHelper;
import com.danydandy.SalonSpa.domain.exception.BadRequestException;
import com.danydandy.SalonSpa.domain.exception.NotFoundException;
import com.danydandy.SalonSpa.domain.model.AuthUser;
import com.danydandy.SalonSpa.domain.model.User;
import com.danydandy.SalonSpa.domain.model.UserServiceCommission;
import com.danydandy.SalonSpa.domain.ports.in.UserServiceCommissionUseCase;
import com.danydandy.SalonSpa.domain.ports.out.ServiceRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.UserRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.UserServiceCommissionRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class UserServiceCommissionServiceImpl implements UserServiceCommissionUseCase {

    private final UserServiceCommissionRepositoryPort userServiceCommissionRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final ServiceRepositoryPort serviceRepositoryPort;

    @Override
    public Mono<List<UserServiceCommission>> findByUserId(Long userId) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> loadUserWithTenantCheck(userId, authUser)
                        .flatMapMany(user -> userServiceCommissionRepositoryPort.findByUserId(userId))
                        .flatMap(this::enrichWithServiceName)
                        .collectList());
    }

    @Override
    public Mono<UserServiceCommission> upsert(Long userId, Long serviceId, Double commissionPercentage) {
        validateCommissionPercentage(commissionPercentage);

        return SecurityHelper.currentUser()
                .flatMap(authUser -> loadUserWithTenantCheck(userId, authUser)
                        .flatMap(user -> validateServiceForUser(user, serviceId))
                        .flatMap(user -> userServiceCommissionRepositoryPort.findByUserIdAndServiceId(userId, serviceId)
                                .flatMap(existing -> {
                                    existing.setCommissionPercentage(commissionPercentage);
                                    return userServiceCommissionRepositoryPort.save(existing);
                                })
                                .switchIfEmpty(Mono.defer(() -> userServiceCommissionRepositoryPort.save(
                                        UserServiceCommission.builder()
                                                .userId(userId)
                                                .serviceId(serviceId)
                                                .commissionPercentage(commissionPercentage)
                                                .build()
                                )))
                                .flatMap(this::enrichWithServiceName)));
    }

    @Override
    public Mono<List<UserServiceCommission>> replaceAll(Long userId, List<UserServiceCommission> commissions) {
        List<UserServiceCommission> items = commissions != null ? commissions : List.of();
        validateUniqueServiceIds(items);

        return SecurityHelper.currentUser()
                .flatMap(authUser -> loadUserWithTenantCheck(userId, authUser))
                .flatMap(user -> validateAllServices(user, items))
                .flatMap(user -> userServiceCommissionRepositoryPort.deleteByUserId(userId).thenReturn(user))
                .flatMap(user -> Flux.fromIterable(items)
                        .concatMap(item -> userServiceCommissionRepositoryPort.save(
                                UserServiceCommission.builder()
                                        .userId(userId)
                                        .serviceId(item.getServiceId())
                                        .commissionPercentage(item.getCommissionPercentage())
                                        .build()
                        ))
                        .flatMap(this::enrichWithServiceName)
                        .collectList());
    }

    @Override
    public Mono<Void> delete(Long userId, Long serviceId) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> loadUserWithTenantCheck(userId, authUser)
                        .flatMap(user -> validateServiceForUser(user, serviceId))
                        .then(userServiceCommissionRepositoryPort.deleteByUserIdAndServiceId(userId, serviceId)));
    }

    private Mono<User> loadUserWithTenantCheck(Long userId, AuthUser authUser) {
        return userRepositoryPort.findById(userId)
                .switchIfEmpty(Mono.error(NotFoundException.forResource("User", userId)))
                .flatMap(user -> SecurityHelper.requireSameSalon(user, user.getSalonId(), authUser, "User", userId));
    }

    private Mono<User> validateServiceForUser(User user, Long serviceId) {
        return serviceRepositoryPort.findById(serviceId)
                .switchIfEmpty(Mono.error(NotFoundException.forResource("Service", serviceId)))
                .flatMap(service -> {
                    if (user.getSalonId() == null || !user.getSalonId().equals(service.getSalonId())) {
                        return Mono.error(new BadRequestException(
                                "Service does not belong to the same salon as the user"));
                    }
                    return Mono.just(user);
                });
    }

    private Mono<User> validateAllServices(User user, List<UserServiceCommission> items) {
        if (items.isEmpty()) {
            return Mono.just(user);
        }
        return Flux.fromIterable(items)
                .concatMap(item -> validateServiceForUser(user, item.getServiceId()))
                .then(Mono.just(user));
    }

    private Mono<UserServiceCommission> enrichWithServiceName(UserServiceCommission commission) {
        return serviceRepositoryPort.findById(commission.getServiceId())
                .map(service -> {
                    commission.setServiceName(service.getName());
                    return commission;
                })
                .defaultIfEmpty(commission);
    }

    private void validateCommissionPercentage(Double commissionPercentage) {
        if (commissionPercentage == null) {
            throw new BadRequestException("Commission percentage is required");
        }
        if (commissionPercentage < 0 || commissionPercentage > 100) {
            throw new BadRequestException("Commission percentage must be between 0 and 100");
        }
    }

    private void validateUniqueServiceIds(List<UserServiceCommission> commissions) {
        Set<Long> serviceIds = new HashSet<>();
        for (UserServiceCommission commission : commissions) {
            validateCommissionPercentage(commission.getCommissionPercentage());
            if (commission.getServiceId() == null) {
                throw new BadRequestException("Service id is required");
            }
            if (!serviceIds.add(commission.getServiceId())) {
                throw new BadRequestException("Duplicate service id in commissions list: " + commission.getServiceId());
            }
        }
    }
}
