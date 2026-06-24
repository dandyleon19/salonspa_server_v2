package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.application.dto.response.AppointmentResponse;
import com.danydandy.SalonSpa.application.dto.response.DashboardResponse;
import com.danydandy.SalonSpa.application.security.SecurityHelper;
import com.danydandy.SalonSpa.domain.model.Appointment;
import com.danydandy.SalonSpa.domain.model.AppointmentStatus;
import com.danydandy.SalonSpa.domain.ports.in.DashboardUseCase;
import com.danydandy.SalonSpa.domain.ports.out.AppointmentRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.DashboardRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.AppointmentMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardUseCase {

    private static final int TODAY_SCHEDULE_LIMIT = 50;
    private static final int UPCOMING_LIMIT = 10;

    private final DashboardRepositoryPort dashboardRepositoryPort;
    private final AppointmentRepositoryPort appointmentRepositoryPort;
    private final AppointmentEnricher appointmentEnricher;
    private final AppointmentMapper appointmentMapper;

    @Override
    public Mono<DashboardResponse> getSummary(LocalDate date, Long branchId, Long userId) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        YearMonth yearMonth = YearMonth.from(targetDate);
        LocalDateTime monthStart = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = yearMonth.plusMonths(1).atDay(1).atStartOfDay();
        LocalDateTime upcomingFrom = LocalDateTime.now();

        return SecurityHelper.currentUser()
                .flatMap(authUser -> {
                    Long salonId = SecurityHelper.isSuperAdmin(authUser) ? null : authUser.getSalonId();
                    return buildSummary(salonId, targetDate, yearMonth, monthStart, monthEnd, upcomingFrom, branchId,
                            userId);
                });
    }

    private Mono<DashboardResponse> buildSummary(Long salonId, LocalDate date, YearMonth yearMonth,
                                                 LocalDateTime monthStart, LocalDateTime monthEnd,
                                                 LocalDateTime upcomingFrom, Long branchId, Long userId) {
        Mono<Long> totalClientsMono = dashboardRepositoryPort.countClients(salonId);
        Mono<Long> activeUsersMono = dashboardRepositoryPort.countActiveUsers(salonId);
        Mono<Long> todayAppointmentsMono = dashboardRepositoryPort.countAppointmentsOnDate(salonId, date, branchId,
                userId);
        Mono<Long> todayScheduledMono = countTodayByStatus(salonId, date, AppointmentStatus.SCHEDULED, branchId,
                userId);
        Mono<Long> todayConfirmedMono = countTodayByStatus(salonId, date, AppointmentStatus.CONFIRMED, branchId,
                userId);
        Mono<Long> todayInProgressMono = countTodayByStatus(salonId, date, AppointmentStatus.IN_PROGRESS, branchId,
                userId);
        Mono<Long> todayCompletedMono = countTodayByStatus(salonId, date, AppointmentStatus.COMPLETED, branchId,
                userId);
        Mono<Map<String, Long>> monthByStatusMono = dashboardRepositoryPort.countAppointmentsByStatusInMonth(salonId,
                monthStart, monthEnd, branchId, userId);
        Mono<List<Appointment>> todayScheduleMono = loadTodaySchedule(salonId, date, branchId, userId);
        Mono<List<Appointment>> upcomingMono = appointmentRepositoryPort.findUpcoming(salonId, branchId, userId,
                        upcomingFrom, UPCOMING_LIMIT)
                .concatMap(appointmentEnricher::enrich)
                .collectList();

        return Mono.zip(
                Mono.zip(
                        totalClientsMono,
                        activeUsersMono,
                        todayAppointmentsMono,
                        todayScheduledMono,
                        todayConfirmedMono,
                        todayInProgressMono,
                        todayCompletedMono,
                        monthByStatusMono
                ),
                todayScheduleMono,
                upcomingMono
        ).map(tuple -> {
            var counts = tuple.getT1();
            Map<String, Long> appointmentsByStatus = counts.getT8();
            long monthAppointments = appointmentsByStatus.values().stream().mapToLong(Long::longValue).sum();
            long monthCancelled = appointmentsByStatus.getOrDefault(AppointmentStatus.CANCELLED.name(), 0L);
            long monthNoShow = appointmentsByStatus.getOrDefault(AppointmentStatus.NO_SHOW.name(), 0L);

            return new DashboardResponse(
                    date,
                    yearMonth.getYear(),
                    yearMonth.getMonthValue(),
                    counts.getT1(),
                    counts.getT2(),
                    counts.getT3(),
                    counts.getT4(),
                    counts.getT5(),
                    counts.getT6(),
                    counts.getT7(),
                    monthAppointments,
                    monthCancelled,
                    monthNoShow,
                    appointmentsByStatus,
                    toResponses(tuple.getT2()),
                    toResponses(tuple.getT3())
            );
        });
    }

    private Mono<Long> countTodayByStatus(Long salonId, LocalDate date, AppointmentStatus status, Long branchId,
                                        Long userId) {
        return dashboardRepositoryPort.countAppointmentsOnDateByStatus(salonId, date, status.name(), branchId, userId);
    }

    private Mono<List<Appointment>> loadTodaySchedule(Long salonId, LocalDate date, Long branchId, Long userId) {
        if (salonId == null) {
            return appointmentRepositoryPort.findAll(0, TODAY_SCHEDULE_LIMIT, branchId, userId, null, date, null)
                    .concatMap(appointmentEnricher::enrich)
                    .collectList();
        }
        return appointmentRepositoryPort.findBySalonId(salonId, 0, TODAY_SCHEDULE_LIMIT, branchId, userId, null, date,
                        null)
                .concatMap(appointmentEnricher::enrich)
                .collectList();
    }

    private List<AppointmentResponse> toResponses(List<Appointment> appointments) {
        return appointments.stream().map(appointmentMapper::toResponse).toList();
    }
}
