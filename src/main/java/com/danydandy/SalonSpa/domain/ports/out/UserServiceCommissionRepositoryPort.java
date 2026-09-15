package com.danydandy.SalonSpa.domain.ports.out;

import com.danydandy.SalonSpa.domain.model.UserServiceCommission;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserServiceCommissionRepositoryPort {
    Mono<UserServiceCommission> save(UserServiceCommission commission);

    Flux<UserServiceCommission> findByUserId(Long userId);

    Mono<UserServiceCommission> findByUserIdAndServiceId(Long userId, Long serviceId);

    Mono<Void> deleteByUserIdAndServiceId(Long userId, Long serviceId);

    Mono<Void> deleteByUserId(Long userId);
}
