package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.ProductCategoryEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductCategoryRepository extends R2dbcRepository<ProductCategoryEntity, Long> {

    @Query("""
            SELECT id, name, description, long_description, salon_id, created_at, updated_at
            FROM product_categories
            WHERE (
                :search IS NULL
                OR name ILIKE :search
                OR description ILIKE :search
                OR long_description ILIKE :search
            )
            ORDER BY created_at ASC, id ASC
            LIMIT :limit OFFSET :offset
            """)
    Flux<ProductCategoryEntity> findPage(String search, int limit, long offset);

    @Query("""
            SELECT COUNT(*)
            FROM product_categories
            WHERE (
                :search IS NULL
                OR name ILIKE :search
                OR description ILIKE :search
                OR long_description ILIKE :search
            )
            """)
    Mono<Long> countFiltered(String search);

    @Query("""
            SELECT id, name, description, long_description, salon_id, created_at, updated_at
            FROM product_categories
            WHERE salon_id = :salonId
              AND (
                  :search IS NULL
                  OR name ILIKE :search
                  OR description ILIKE :search
                  OR long_description ILIKE :search
              )
            ORDER BY created_at ASC, id ASC
            LIMIT :limit OFFSET :offset
            """)
    Flux<ProductCategoryEntity> findPageBySalonId(Long salonId, String search, int limit, long offset);

    @Query("""
            SELECT COUNT(*)
            FROM product_categories
            WHERE salon_id = :salonId
              AND (
                  :search IS NULL
                  OR name ILIKE :search
                  OR description ILIKE :search
                  OR long_description ILIKE :search
              )
            """)
    Mono<Long> countBySalonId(Long salonId, String search);
}
