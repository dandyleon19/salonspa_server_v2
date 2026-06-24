package com.danydandy.SalonSpa.application.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record DashboardResponse(
        LocalDate date,
        int year,
        int month,
        long totalClients,
        long activeUsers,
        long todayAppointments,
        long todayScheduled,
        long todayConfirmed,
        long todayInProgress,
        long todayCompleted,
        long monthAppointments,
        long monthCancelled,
        long monthNoShow,
        Map<String, Long> appointmentsByStatus,
        List<AppointmentResponse> todaySchedule,
        List<AppointmentResponse> upcomingAppointments
) {
}
