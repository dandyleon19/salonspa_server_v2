package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.UserServiceCommissionEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserServiceCommissionRepository extends R2dbcRepository<UserServiceCommissionEntity, Long> {

    @Query("""
            SELECT id, user_id, service_id, commission_percentage, created_at, updated_at
            FROM user_service_commissions
            WHERE user_id = :userId
            ORDER BY service_id ASC
            """)
    Flux<UserServiceCommissionEntity> findByUserId(Long userId);

    @Query("""
            SELECT id, user_id, service_id, commission_percentage, created_at, updated_at
            FROM user_service_commissions
            WHERE user_id = :userId AND service_id = :serviceId
            """)
    Mono<UserServiceCommissionEntity> findByUserIdAndServiceId(Long userId, Long serviceId);

    @Query("""
            DELETE FROM user_service_commissions
            WHERE user_id = :userId AND service_id = :serviceId
            """)
    Mono<Void> deleteByUserIdAndServiceId(Long userId, Long serviceId);

    @Query("""
            DELETE FROM user_service_commissions
            WHERE user_id = :userId
            """)
    Mono<Void> deleteByUserId(Long userId);
}
