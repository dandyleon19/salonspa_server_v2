package com.danydandy.SalonSpa.application.dto.response;

import java.math.BigDecimal;

public record ServiceResponse (
        Long id,
        String name,
        String description,
        String longDescription,
        BigDecimal price,
        Boolean isActive,
        Long salonId,
        Long categoryId
) {
}
