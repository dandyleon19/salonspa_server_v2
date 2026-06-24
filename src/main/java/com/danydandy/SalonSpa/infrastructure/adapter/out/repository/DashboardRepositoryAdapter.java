package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.domain.ports.out.DashboardRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class DashboardRepositoryAdapter implements DashboardRepositoryPort {

    private final DashboardRepository dashboardRepository;

    @Override
    public Mono<Long> countClients(Long salonId) {
        return dashboardRepository.countClients(salonId);
    }

    @Override
    public Mono<Long> countActiveUsers(Long salonId) {
        return dashboardRepository.countActiveUsers(salonId);
    }

    @Override
    public Mono<Long> countAppointmentsOnDate(Long salonId, LocalDate date, Long branchId, Long userId) {
        return dashboardRepository.countAppointmentsOnDate(salonId, date, branchId, userId);
    }

    @Override
    public Mono<Long> countAppointmentsOnDateByStatus(Long salonId, LocalDate date, String status, Long branchId,
                                                      Long userId) {
        return dashboardRepository.countAppointmentsOnDateByStatus(salonId, date, status, branchId, userId);
    }

    @Override
    public Mono<Map<String, Long>> countAppointmentsByStatusInMonth(Long salonId, LocalDateTime monthStart,
                                                                    LocalDateTime monthEnd, Long branchId,
                                                                    Long userId) {
        return dashboardRepository.countAppointmentsByStatusInMonth(salonId, monthStart, monthEnd, branchId, userId)
                .collectList()
                .map(rows -> {
                    Map<String, Long> counts = new HashMap<>();
                    for (DashboardStatusCountRow row : rows) {
                        counts.put(row.getStatus(), row.getCount());
                    }
                    return counts;
                });
    }
}
