package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.domain.model.UserServiceCommission;
import com.danydandy.SalonSpa.domain.ports.out.UserServiceCommissionRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.UserServiceCommissionMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserServiceCommissionRepositoryAdapter implements UserServiceCommissionRepositoryPort {

    private final UserServiceCommissionRepository repository;
    private final UserServiceCommissionMapper mapper;

    @Override
    public Mono<UserServiceCommission> save(UserServiceCommission commission) {
        return repository.save(mapper.toEntity(commission))
                .map(mapper::toDomain);
    }

    @Override
    public Flux<UserServiceCommission> findByUserId(Long userId) {
        return repository.findByUserId(userId)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<UserServiceCommission> findByUserIdAndServiceId(Long userId, Long serviceId) {
        return repository.findByUserIdAndServiceId(userId, serviceId)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteByUserIdAndServiceId(Long userId, Long serviceId) {
        return repository.deleteByUserIdAndServiceId(userId, serviceId);
    }

    @Override
    public Mono<Void> deleteByUserId(Long userId) {
        return repository.deleteByUserId(userId);
    }
}
