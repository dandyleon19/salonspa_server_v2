package com.danydandy.SalonSpa.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Sale {
    private Long id;
    private Long salonId;
    private Long branchId;
    private Long clientId;
    private Long registeredByUserId;
    private Long appointmentId;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private BigDecimal amountPaid;
    private SaleStatus status;
    private String notes;
    private LocalDateTime soldAt;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    private String clientName;
    private String branchName;
    private String registeredByUserName;
    private List<SaleItem> items;
    private List<SalePayment> payments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
