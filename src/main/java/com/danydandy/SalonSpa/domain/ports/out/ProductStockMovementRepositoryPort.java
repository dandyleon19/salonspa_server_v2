package com.danydandy.SalonSpa.domain.ports.out;

import com.danydandy.SalonSpa.domain.model.ProductStockMovement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductStockMovementRepositoryPort {
    Mono<ProductStockMovement> save(ProductStockMovement movement);
    Flux<ProductStockMovement> findPageByProductId(Long productId, int page, int size);
    Mono<Long> countByProductId(Long productId);
}
