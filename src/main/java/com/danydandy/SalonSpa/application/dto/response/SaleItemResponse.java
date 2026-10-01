package com.danydandy.SalonSpa.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SaleItemResponse(
        Long id,
        Long saleId,
        Long serviceId,
        Long productId,
        Long userId,
        Long appointmentId,
        String serviceName,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal discountAmount,
        BigDecimal lineTotal,
        String userName,
        LocalDateTime createdAt
) {
}
