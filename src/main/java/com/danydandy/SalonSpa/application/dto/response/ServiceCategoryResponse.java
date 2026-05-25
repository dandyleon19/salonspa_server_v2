package com.danydandy.SalonSpa.application.dto.response;

import java.math.BigDecimal;

public record ServiceCategoryResponse (
        Long id,
        String name,
        String description,
        String longDescription,
        Long salonId
) {
}
