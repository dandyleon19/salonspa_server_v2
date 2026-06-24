package com.danydandy.SalonSpa.domain.ports.in;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.domain.model.Appointment;
import com.danydandy.SalonSpa.domain.model.AppointmentStatus;

import java.time.LocalDate;
import java.util.List;

import reactor.core.publisher.Mono;

public interface AppointmentUseCase {
    Mono<Appointment> create(Appointment appointment);
    Mono<PageResponse<Appointment>> findPage(int page, int size, Long branchId, Long userId, Long clientId,
                                             LocalDate date, AppointmentStatus status);
    Mono<List<Appointment>> findByMonth(int year, int month, Long branchId, Long userId, AppointmentStatus status);
    Mono<Appointment> findById(Long id);
    Mono<Appointment> update(Long id, Appointment appointment);
    Mono<Void> delete(Long id);
    Mono<Appointment> scheduleFollowUp(Appointment appointment);
}
