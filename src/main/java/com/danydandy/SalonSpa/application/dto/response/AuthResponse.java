package com.danydandy.SalonSpa.application.dto.response;

import com.danydandy.SalonSpa.domain.model.Role;

public record AuthResponse(
        String token,
        String refreshToken,
        Long userId,
        String email,
        Long salonId,
        Role role
) {
}
