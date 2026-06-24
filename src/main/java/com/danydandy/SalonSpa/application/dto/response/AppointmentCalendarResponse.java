package com.danydandy.SalonSpa.application.dto.response;

import java.time.LocalDate;
import java.util.List;

public record AppointmentCalendarResponse(
        int year,
        int month,
        LocalDate from,
        LocalDate to,
        int total,
        List<AppointmentResponse> appointments
) {
}
