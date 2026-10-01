package com.danydandy.SalonSpa.infrastructure.adapter.out.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("sale_items")
public class SaleItemEntity {
    @Id
    private Long id;
    @Column("sale_id")
    private Long saleId;
    @Column("service_id")
    private Long serviceId;
    @Column("product_id")
    private Long productId;
    @Column("user_id")
    private Long userId;
    @Column("appointment_id")
    private Long appointmentId;
    @Column("service_name")
    private String serviceName;
    @Column("product_name")
    private String productName;
    private Integer quantity;
    @Column("unit_price")
    private BigDecimal unitPrice;
    @Column("discount_amount")
    private BigDecimal discountAmount;
    @Column("line_total")
    private BigDecimal lineTotal;
    @Column("created_at")
    private LocalDateTime createdAt;
}
