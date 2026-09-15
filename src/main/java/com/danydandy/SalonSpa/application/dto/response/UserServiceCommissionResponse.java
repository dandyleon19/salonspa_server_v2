package com.danydandy.SalonSpa.application.dto.response;

import java.time.LocalDateTime;

public record UserServiceCommissionResponse(
        Long id,
        Long userId,
        Long serviceId,
        String serviceName,
        Double commissionPercentage,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
