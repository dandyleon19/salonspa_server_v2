package com.danydandy.SalonSpa.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ClinicalRecordResponse(
        Long id,
        String diagnosis,
        String treatment,
        String observations,
        LocalDateTime sessionDate,
        Long clientId,
        Long userId,
        Long serviceId,
        Long branchId,
        String userName,
        String branchName,
        List<String> associatedServices,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
