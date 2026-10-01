package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.ProductStockMovementEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductStockMovementRepository extends R2dbcRepository<ProductStockMovementEntity, Long> {

    @Query("""
            SELECT id, product_id, movement_type, quantity_delta, resulting_stock, reason,
                   sale_id, created_by_user_id, created_at
            FROM product_stock_movements
            WHERE product_id = :productId
            ORDER BY created_at DESC, id DESC
            LIMIT :limit OFFSET :offset
            """)
    Flux<ProductStockMovementEntity> findPageByProductId(Long productId, int limit, long offset);

    Mono<Long> countByProductId(Long productId);
}
