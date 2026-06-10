package com.danydandy.SalonSpa.application.dto.response;

public record ServiceCategoryResponse (
        Long id,
        String name,
        String description,
        String longDescription,
        Long salonId
) {
}
