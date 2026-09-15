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
@Table("sales")
public class SaleEntity {
    @Id
    private Long id;
    @Column("salon_id")
    private Long salonId;
    @Column("branch_id")
    private Long branchId;
    @Column("client_id")
    private Long clientId;
    @Column("registered_by_user_id")
    private Long registeredByUserId;
    @Column("appointment_id")
    private Long appointmentId;
    private BigDecimal subtotal;
    @Column("discount_amount")
    private BigDecimal discountAmount;
    @Column("total_amount")
    private BigDecimal totalAmount;
    @Column("amount_paid")
    private BigDecimal amountPaid;
    private String status;
    private String notes;
    @Column("sold_at")
    private LocalDateTime soldAt;
    @Column("cancelled_at")
    private LocalDateTime cancelledAt;
    @Column("cancellation_reason")
    private String cancellationReason;
    @Column("created_at")
    private LocalDateTime createdAt;
    @Column("updated_at")
    private LocalDateTime updatedAt;
}
