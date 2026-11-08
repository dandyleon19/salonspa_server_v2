package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.ProductEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository extends R2dbcRepository<ProductEntity, Long> {

    Flux<ProductEntity> findByCategoryIdOrderByCreatedAtAscIdAsc(Long categoryId);

    Flux<ProductEntity> findAllByOrderByCreatedAtAscIdAsc(Pageable pageable);

    Flux<ProductEntity> findBySalonIdOrderByCreatedAtAscIdAsc(Long salonId, Pageable pageable);

    Mono<Long> countBySalonId(Long salonId);

    @Query("""
            SELECT *
            FROM products
            WHERE (
                :search IS NULL
                OR name ILIKE :search
                OR description ILIKE :search
            )
            ORDER BY created_at ASC, id ASC
            LIMIT :limit OFFSET :offset
            """)
    Flux<ProductEntity> findPage(String search, int limit, long offset);

    @Query("""
            SELECT COUNT(*)
            FROM products
            WHERE (
                :search IS NULL
                OR name ILIKE :search
                OR description ILIKE :search
            )
            """)
    Mono<Long> countFiltered(String search);

    @Query("""
            SELECT *
            FROM products
            WHERE salon_id = :salonId
              AND (
                  :search IS NULL
                  OR name ILIKE :search
                  OR description ILIKE :search
              )
            ORDER BY created_at ASC, id ASC
            LIMIT :limit OFFSET :offset
            """)
    Flux<ProductEntity> findPageBySalonId(Long salonId, String search, int limit, long offset);

    @Query("""
            SELECT COUNT(*)
            FROM products
            WHERE salon_id = :salonId
              AND (
                  :search IS NULL
                  OR name ILIKE :search
                  OR description ILIKE :search
              )
            """)
    Mono<Long> countFilteredBySalonId(Long salonId, String search);

    /**
     * Atomically applies a delta (positive or negative) to a product's stock and returns the
     * resulting quantity. Not {@code @Modifying}: the RETURNING clause makes this a row-producing
     * statement, so R2DBC maps it like a regular query. Returns empty if the resulting stock would
     * go negative (the WHERE guard excludes the row, so no update happens and nothing is returned).
     */
    @Query("""
            UPDATE products
            SET stock_quantity = stock_quantity + :delta, updated_at = CURRENT_TIMESTAMP
            WHERE id = :id AND stock_quantity + :delta >= 0
            RETURNING stock_quantity
            """)
    Mono<Integer> applyStockDelta(Long id, Integer delta);
}
