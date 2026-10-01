package com.danydandy.SalonSpa.application.dto.response;

import com.danydandy.SalonSpa.domain.model.StockMovementType;

import java.time.LocalDateTime;

public record ProductStockMovementResponse(
        Long id,
        Long productId,
        StockMovementType movementType,
        Integer quantityDelta,
        Integer resultingStock,
        String reason,
        Long saleId,
        Long createdByUserId,
        String createdByUserName,
        LocalDateTime createdAt
) {
}
