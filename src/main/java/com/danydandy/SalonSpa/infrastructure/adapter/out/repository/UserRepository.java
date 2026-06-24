package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.UserEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<UserEntity, Long> {

    Mono<UserEntity> findByEmail(String email);

    @Query("""
            SELECT id, first_name, last_name, email, password, is_active, commission_percentage,
                   role, salon_id, created_at, updated_at
            FROM users
            WHERE (:isActive IS NULL OR is_active = :isActive)
              AND (:role IS NULL OR role = :role)
              AND (
                  :search IS NULL
                  OR first_name ILIKE :search
                  OR last_name ILIKE :search
                  OR email ILIKE :search
                  OR role ILIKE :search
              )
            ORDER BY created_at ASC
            LIMIT :limit OFFSET :offset
            """)
    Flux<UserEntity> findPage(Boolean isActive, String role, String search, int limit, long offset);

    @Query("""
            SELECT COUNT(*)
            FROM users
            WHERE (:isActive IS NULL OR is_active = :isActive)
              AND (:role IS NULL OR role = :role)
              AND (
                  :search IS NULL
                  OR first_name ILIKE :search
                  OR last_name ILIKE :search
                  OR email ILIKE :search
                  OR role ILIKE :search
              )
            """)
    Mono<Long> countFiltered(Boolean isActive, String role, String search);

    @Query("""
            SELECT id, first_name, last_name, email, password, is_active, commission_percentage,
                   role, salon_id, created_at, updated_at
            FROM users
            WHERE salon_id = :salonId
              AND (:isActive IS NULL OR is_active = :isActive)
              AND (:role IS NULL OR role = :role)
              AND (
                  :search IS NULL
                  OR first_name ILIKE :search
                  OR last_name ILIKE :search
                  OR email ILIKE :search
                  OR role ILIKE :search
              )
            ORDER BY created_at ASC
            LIMIT :limit OFFSET :offset
            """)
    Flux<UserEntity> findPageBySalonId(Long salonId, Boolean isActive, String role, String search, int limit,
                                       long offset);

    @Query("""
            SELECT COUNT(*)
            FROM users
            WHERE salon_id = :salonId
              AND (:isActive IS NULL OR is_active = :isActive)
              AND (:role IS NULL OR role = :role)
              AND (
                  :search IS NULL
                  OR first_name ILIKE :search
                  OR last_name ILIKE :search
                  OR email ILIKE :search
                  OR role ILIKE :search
              )
            """)
    Mono<Long> countBySalonId(Long salonId, Boolean isActive, String role, String search);
}
