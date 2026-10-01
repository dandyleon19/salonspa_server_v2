package com.danydandy.SalonSpa.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductStockMovement {
    private Long id;
    private Long productId;
    private StockMovementType movementType;
    private Integer quantityDelta;
    private Integer resultingStock;
    private String reason;
    private Long saleId;
    private Long createdByUserId;
    private String createdByUserName;
    private LocalDateTime createdAt;
}
