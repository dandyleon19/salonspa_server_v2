package com.danydandy.SalonSpa.domain.ports.out;

import com.danydandy.SalonSpa.domain.model.Appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AppointmentRepositoryPort {
    Mono<Appointment> save(Appointment appointment);
    Mono<Appointment> findById(Long id);
    Mono<Void> deleteById(Long id);
    Flux<Appointment> findAll(int page, int size, Long branchId, Long userId, Long clientId, LocalDate date,
                              String status);
    Mono<Long> countAll(Long branchId, Long userId, Long clientId, LocalDate date, String status);
    Flux<Appointment> findBySalonId(Long salonId, int page, int size, Long branchId, Long userId, Long clientId,
                                    LocalDate date, String status);
    Mono<Long> countBySalonId(Long salonId, Long branchId, Long userId, Long clientId, LocalDate date, String status);
    Flux<Appointment> findByPeriod(Long branchId, Long userId, String status, LocalDateTime periodStart,
                                   LocalDateTime periodEnd, int limit);
    Flux<Appointment> findBySalonIdAndPeriod(Long salonId, Long branchId, Long userId, String status,
                                             LocalDateTime periodStart, LocalDateTime periodEnd, int limit);
    Mono<Long> countByPeriod(Long branchId, Long userId, String status, LocalDateTime periodStart,
                             LocalDateTime periodEnd);
    Mono<Long> countBySalonIdAndPeriod(Long salonId, Long branchId, Long userId, String status,
                                       LocalDateTime periodStart, LocalDateTime periodEnd);
    Flux<Appointment> findUpcoming(Long salonId, Long branchId, Long userId, LocalDateTime from, int limit);
    Mono<Long> countOverlapping(Long userId, java.time.LocalDateTime startAt, java.time.LocalDateTime endAt, Long excludeId);
}
