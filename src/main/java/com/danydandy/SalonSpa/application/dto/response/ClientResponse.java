package com.danydandy.SalonSpa.application.dto.response;

import com.danydandy.SalonSpa.domain.model.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ClientResponse(
        Long id,
        String firstName,
        String lastName,
        String fullName,
        String documentNumber,
        String phone,
        String email,
        LocalDate birthDate,
        Gender gender,
        Long salonId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
