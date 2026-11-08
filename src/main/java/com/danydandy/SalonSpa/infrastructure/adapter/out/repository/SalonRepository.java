package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.SalonEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SalonRepository extends R2dbcRepository<SalonEntity, Long> {
    Flux<SalonEntity> findAllByOrderByCreatedAtAscIdAsc(Pageable pageable);

    @Query("""
            SELECT *
            FROM salons
            WHERE (
                :search IS NULL
                OR name ILIKE :search
                OR social_reason ILIKE :search
                OR ruc_number ILIKE :search
            )
            ORDER BY created_at ASC, id ASC
            LIMIT :limit OFFSET :offset
            """)
    Flux<SalonEntity> findPage(String search, int limit, long offset);

    @Query("""
            SELECT COUNT(*)
            FROM salons
            WHERE (
                :search IS NULL
                OR name ILIKE :search
                OR social_reason ILIKE :search
                OR ruc_number ILIKE :search
            )
            """)
    Mono<Long> countFiltered(String search);
}
