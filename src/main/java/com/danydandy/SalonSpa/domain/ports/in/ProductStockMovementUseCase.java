package com.danydandy.SalonSpa.domain.ports.in;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.domain.model.ProductStockMovement;
import com.danydandy.SalonSpa.domain.model.StockMovementType;
import reactor.core.publisher.Mono;

public interface ProductStockMovementUseCase {
    Mono<ProductStockMovement> create(Long productId, StockMovementType movementType, Integer quantityDelta, String reason);
    Mono<PageResponse<ProductStockMovement>> findPage(Long productId, int page, int size);
}
