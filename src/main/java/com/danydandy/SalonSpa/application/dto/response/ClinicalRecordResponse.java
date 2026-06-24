package com.danydandy.SalonSpa.application.dto.response;

import com.danydandy.SalonSpa.application.dto.response.AppointmentResponse;

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
        AppointmentResponse nextAppointment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
