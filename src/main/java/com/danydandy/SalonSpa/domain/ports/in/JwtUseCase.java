package com.danydandy.SalonSpa.domain.ports.in;

import com.danydandy.SalonSpa.domain.model.Role;

public interface JwtUseCase {
    String generateToken(Long userId, Long salonId, Role role);
    Long extractUserId(String token);
    Long extractSalonId(String token);
    String extractRole(String token);
}
