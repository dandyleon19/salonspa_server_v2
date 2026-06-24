package com.danydandy.SalonSpa.application.dto.response;

import com.danydandy.SalonSpa.domain.model.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long clientId,
        Long userId,
        Long branchId,
        Long salonId,
        Long serviceId,
        LocalDateTime startAt,
        LocalDateTime endAt,
        String status,
        String notes,
        String clientName,
        String clientPhone,
        String clientEmail,
        LocalDate clientBirthDate,
        Gender clientGender,
        String userName,
        String branchName,
        String serviceName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
