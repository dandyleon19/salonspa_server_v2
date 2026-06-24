package com.danydandy.SalonSpa.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ServiceResponse(
        Long id,
        String name,
        String description,
        String longDescription,
        BigDecimal price,
        Integer durationMinutes,
        Boolean isActive,
        Long salonId,
        Long categoryId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
