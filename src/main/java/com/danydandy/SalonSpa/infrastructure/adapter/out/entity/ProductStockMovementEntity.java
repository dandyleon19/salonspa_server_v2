package com.danydandy.SalonSpa.infrastructure.adapter.out.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("product_stock_movements")
public class ProductStockMovementEntity {
    @Id
    private Long id;
    @Column("product_id")
    private Long productId;
    @Column("movement_type")
    private String movementType;
    @Column("quantity_delta")
    private Integer quantityDelta;
    @Column("resulting_stock")
    private Integer resultingStock;
    private String reason;
    @Column("sale_id")
    private Long saleId;
    @Column("created_by_user_id")
    private Long createdByUserId;
    @Column("created_at")
    private LocalDateTime createdAt;
}
