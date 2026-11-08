package com.danydandy.SalonSpa.application.dto.response;

import com.danydandy.SalonSpa.domain.model.Role;
import lombok.Builder;

import java.time.LocalDateTime;

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
        String salonName,
        String salonLogoUrl,
        /** When the user last signed in or renewed their session (null = never). */
        LocalDateTime lastLoginAt
) {
}
