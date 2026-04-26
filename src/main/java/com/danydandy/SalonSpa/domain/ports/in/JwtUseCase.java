package com.danydandy.SalonSpa.domain.ports.in;

public interface JwtUseCase {
    String generateToken(Long userId, Long salonId);
    Long extractUserId(String token);
    Long extractSalonId(String token);
}
