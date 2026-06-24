package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.domain.model.Appointment;
import com.danydandy.SalonSpa.domain.ports.out.AppointmentRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.AppointmentMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class AppointmentRepositoryAdapter implements AppointmentRepositoryPort {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    @Override
    public Mono<Appointment> save(Appointment appointment) {
        return appointmentRepository.save(appointmentMapper.toEntity(appointment))
                .map(appointmentMapper::toDomain);
    }

    @Override
    public Mono<Appointment> findById(Long id) {
        return appointmentRepository.findById(id)
                .map(appointmentMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return appointmentRepository.deleteById(id);
    }

    @Override
    public Flux<Appointment> findAll(int page, int size, Long branchId, Long userId, Long clientId, LocalDate date,
                                   String status) {
        long offset = (long) page * size;
        return appointmentRepository.findPage(branchId, userId, clientId, date, status, size, offset)
                .map(appointmentMapper::toDomain);
    }

    @Override
    public Mono<Long> countAll(Long branchId, Long userId, Long clientId, LocalDate date, String status) {
        return appointmentRepository.countFiltered(branchId, userId, clientId, date, status);
    }

    @Override
    public Flux<Appointment> findBySalonId(Long salonId, int page, int size, Long branchId, Long userId,
                                           Long clientId, LocalDate date, String status) {
        long offset = (long) page * size;
        return appointmentRepository.findPageBySalonId(salonId, branchId, userId, clientId, date, status, size, offset)
                .map(appointmentMapper::toDomain);
    }

    @Override
    public Mono<Long> countBySalonId(Long salonId, Long branchId, Long userId, Long clientId, LocalDate date,
                                     String status) {
        return appointmentRepository.countBySalonId(salonId, branchId, userId, clientId, date, status);
    }

    @Override
    public Flux<Appointment> findByPeriod(Long branchId, Long userId, String status, LocalDateTime periodStart,
                                          LocalDateTime periodEnd, int limit) {
        return appointmentRepository.findByPeriod(branchId, userId, status, periodStart, periodEnd, limit)
                .map(appointmentMapper::toDomain);
    }

    @Override
    public Flux<Appointment> findBySalonIdAndPeriod(Long salonId, Long branchId, Long userId, String status,
                                                    LocalDateTime periodStart, LocalDateTime periodEnd, int limit) {
        return appointmentRepository.findBySalonIdAndPeriod(salonId, branchId, userId, status, periodStart, periodEnd,
                        limit)
                .map(appointmentMapper::toDomain);
    }

    @Override
    public Mono<Long> countByPeriod(Long branchId, Long userId, String status, LocalDateTime periodStart,
                                    LocalDateTime periodEnd) {
        return appointmentRepository.countByPeriod(branchId, userId, status, periodStart, periodEnd);
    }

    @Override
    public Mono<Long> countBySalonIdAndPeriod(Long salonId, Long branchId, Long userId, String status,
                                                LocalDateTime periodStart, LocalDateTime periodEnd) {
        return appointmentRepository.countBySalonIdAndPeriod(salonId, branchId, userId, status, periodStart, periodEnd);
    }

    @Override
    public Flux<Appointment> findUpcoming(Long salonId, Long branchId, Long userId, LocalDateTime from, int limit) {
        return appointmentRepository.findUpcoming(salonId, branchId, userId, from, limit)
                .map(appointmentMapper::toDomain);
    }

    @Override
    public Mono<Long> countOverlapping(Long userId, java.time.LocalDateTime startAt, java.time.LocalDateTime endAt,
                                       Long excludeId) {
        return appointmentRepository.countOverlapping(userId, startAt, endAt, excludeId);
    }
}
