package com.danydandy.SalonSpa.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ProductCategoryResponse(
        Long id,
        String name,
        String description,
        String longDescription,
        Long salonId,
        List<ProductResponse> products,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
