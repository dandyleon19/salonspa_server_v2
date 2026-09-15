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
@Table("sale_payments")
public class SalePaymentEntity {
    @Id
    private Long id;
    @Column("sale_id")
    private Long saleId;
    private BigDecimal amount;
    @Column("payment_method")
    private String paymentMethod;
    private String reference;
    @Column("paid_at")
    private LocalDateTime paidAt;
    @Column("created_at")
    private LocalDateTime createdAt;
}
