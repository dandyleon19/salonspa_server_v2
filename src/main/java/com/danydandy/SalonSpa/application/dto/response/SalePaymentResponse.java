package com.danydandy.SalonSpa.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SalePaymentResponse(
        Long id,
        Long saleId,
        BigDecimal amount,
        String paymentMethod,
        String reference,
        LocalDateTime paidAt,
        LocalDateTime createdAt
) {
}
