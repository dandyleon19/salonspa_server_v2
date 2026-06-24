package com.danydandy.SalonSpa.domain.ports.out;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public interface DashboardRepositoryPort {
    Mono<Long> countClients(Long salonId);
    Mono<Long> countActiveUsers(Long salonId);
    Mono<Long> countAppointmentsOnDate(Long salonId, LocalDate date, Long branchId, Long userId);
    Mono<Long> countAppointmentsOnDateByStatus(Long salonId, LocalDate date, String status, Long branchId,
                                                 Long userId);
    Mono<Map<String, Long>> countAppointmentsByStatusInMonth(Long salonId, LocalDateTime monthStart,
                                                             LocalDateTime monthEnd, Long branchId, Long userId);
}
