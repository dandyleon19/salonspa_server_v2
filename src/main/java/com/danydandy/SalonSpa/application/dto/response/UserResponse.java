package com.danydandy.SalonSpa.application.dto.response;

import lombok.Builder;

@Builder
public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String fullName,
        String email,
        Boolean isActive,
        Double commissionPercentage,
        String salonName
) {
}
