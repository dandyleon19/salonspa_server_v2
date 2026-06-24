package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.BranchEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchRepository extends R2dbcRepository<BranchEntity, Long> {

    Flux<BranchEntity> findBySalonIdOrderByCreatedAtAsc(Long salonId);

    @Query("""
            SELECT id, name, address, city, salon_id, created_at, updated_at
            FROM branches
            WHERE (
                :search IS NULL
                OR name ILIKE :search
                OR address ILIKE :search
                OR city ILIKE :search
            )
            ORDER BY created_at ASC
            LIMIT :limit OFFSET :offset
            """)
    Flux<BranchEntity> findPage(String search, int limit, long offset);

    @Query("""
            SELECT COUNT(*)
            FROM branches
            WHERE (
                :search IS NULL
                OR name ILIKE :search
                OR address ILIKE :search
                OR city ILIKE :search
            )
            """)
    Mono<Long> countFiltered(String search);

    @Query("""
            SELECT id, name, address, city, salon_id, created_at, updated_at
            FROM branches
            WHERE salon_id = :salonId
              AND (
                  :search IS NULL
                  OR name ILIKE :search
                  OR address ILIKE :search
                  OR city ILIKE :search
              )
            ORDER BY created_at ASC
            LIMIT :limit OFFSET :offset
            """)
    Flux<BranchEntity> findPageBySalonId(Long salonId, String search, int limit, long offset);

    @Query("""
            SELECT COUNT(*)
            FROM branches
            WHERE salon_id = :salonId
              AND (
                  :search IS NULL
                  OR name ILIKE :search
                  OR address ILIKE :search
                  OR city ILIKE :search
              )
            """)
    Mono<Long> countBySalonId(Long salonId, String search);
}
