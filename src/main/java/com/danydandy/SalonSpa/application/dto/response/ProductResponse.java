package com.danydandy.SalonSpa.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        String description,
        String longDescription,
        BigDecimal price,
        Integer stockQuantity,
        Integer lowStockThreshold,
        Boolean isActive,
        String imageUrl,
        Long salonId,
        Long categoryId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
