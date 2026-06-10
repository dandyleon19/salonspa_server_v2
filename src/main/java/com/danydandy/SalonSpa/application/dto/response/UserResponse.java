package com.danydandy.SalonSpa.application.dto.response;

import com.danydandy.SalonSpa.domain.model.Role;
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
        Role role,
        Long salonId,
        String salonName
) {
}
