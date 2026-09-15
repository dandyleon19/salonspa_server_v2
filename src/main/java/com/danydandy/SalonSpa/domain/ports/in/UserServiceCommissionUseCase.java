package com.danydandy.SalonSpa.domain.ports.in;

import com.danydandy.SalonSpa.domain.model.UserServiceCommission;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserServiceCommissionUseCase {
    Mono<List<UserServiceCommission>> findByUserId(Long userId);

    Mono<UserServiceCommission> upsert(Long userId, Long serviceId, Double commissionPercentage);

    Mono<List<UserServiceCommission>> replaceAll(Long userId, List<UserServiceCommission> commissions);

    Mono<Void> delete(Long userId, Long serviceId);
}
