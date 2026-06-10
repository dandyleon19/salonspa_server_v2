package com.danydandy.SalonSpa.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ServiceResponse(
        Long id,
        String name,
        String description,
        String longDescription,
        BigDecimal price,
        Boolean isActive,
        Long salonId,
        Long categoryId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
