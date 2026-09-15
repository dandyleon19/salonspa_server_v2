package com.danydandy.SalonSpa.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SaleResponse(
        Long id,
        Long salonId,
        Long branchId,
        Long clientId,
        Long registeredByUserId,
        Long appointmentId,
        BigDecimal subtotal,
        BigDecimal discountAmount,
        BigDecimal totalAmount,
        BigDecimal amountPaid,
        String status,
        String notes,
        LocalDateTime soldAt,
        LocalDateTime cancelledAt,
        String cancellationReason,
        String clientName,
        String branchName,
        String registeredByUserName,
        List<SaleItemResponse> items,
        List<SalePaymentResponse> payments,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
