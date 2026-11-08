package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.ServiceEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ServiceRepository extends R2dbcRepository<ServiceEntity, Long> {

    Flux<ServiceEntity> findByCategoryIdOrderByCreatedAtAscIdAsc(Long categoryId);

    Flux<ServiceEntity> findAllByOrderByCreatedAtAscIdAsc(Pageable pageable);

    Flux<ServiceEntity> findBySalonIdOrderByCreatedAtAscIdAsc(Long salonId, Pageable pageable);

    Mono<Long> countBySalonId(Long salonId);

    @Query("""
            SELECT *
            FROM services
            WHERE (
                :search IS NULL
                OR name ILIKE :search
                OR description ILIKE :search
            )
            ORDER BY created_at ASC, id ASC
            LIMIT :limit OFFSET :offset
            """)
    Flux<ServiceEntity> findPage(String search, int limit, long offset);

    @Query("""
            SELECT COUNT(*)
            FROM services
            WHERE (
                :search IS NULL
                OR name ILIKE :search
                OR description ILIKE :search
            )
            """)
    Mono<Long> countFiltered(String search);

    @Query("""
            SELECT *
            FROM services
            WHERE salon_id = :salonId
              AND (
                  :search IS NULL
                  OR name ILIKE :search
                  OR description ILIKE :search
              )
            ORDER BY created_at ASC, id ASC
            LIMIT :limit OFFSET :offset
            """)
    Flux<ServiceEntity> findPageBySalonId(Long salonId, String search, int limit, long offset);

    @Query("""
            SELECT COUNT(*)
            FROM services
            WHERE salon_id = :salonId
              AND (
                  :search IS NULL
                  OR name ILIKE :search
                  OR description ILIKE :search
              )
            """)
    Mono<Long> countFilteredBySalonId(Long salonId, String search);
}
