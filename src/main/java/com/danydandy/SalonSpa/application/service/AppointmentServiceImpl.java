package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.application.security.SecurityHelper;
import com.danydandy.SalonSpa.domain.exception.BadRequestException;
import com.danydandy.SalonSpa.domain.exception.ConflictException;
import com.danydandy.SalonSpa.domain.exception.NotFoundException;
import com.danydandy.SalonSpa.domain.model.*;
import com.danydandy.SalonSpa.domain.ports.in.AppointmentUseCase;
import com.danydandy.SalonSpa.domain.ports.out.*;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentUseCase {

    private static final int MAX_CALENDAR_APPOINTMENTS = 500;

    private final AppointmentRepositoryPort appointmentRepositoryPort;
    private final ClientRepositoryPort clientRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final BranchRepositoryPort branchRepositoryPort;
    private final ServiceRepositoryPort serviceRepositoryPort;
    private final AppointmentEnricher appointmentEnricher;

    @Override
    public Mono<Appointment> create(Appointment appointment) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> persistAppointment(appointment, authUser.getSalonId(), null));
    }

    @Override
    public Mono<Appointment> scheduleFollowUp(Appointment appointment) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> persistAppointment(appointment, authUser.getSalonId(), null));
    }

    @Override
    public Mono<PageResponse<Appointment>> findPage(int page, int size, Long branchId, Long userId, Long clientId,
                                                      LocalDate date, AppointmentStatus status) {
        String statusFilter = status != null ? status.name() : null;
        return SecurityHelper.currentUser()
                .flatMap(authUser -> {
                    if (SecurityHelper.isSuperAdmin(authUser)) {
                        return paginateAll(page, size, branchId, userId, clientId, date, statusFilter);
                    }
                    return paginateBySalonId(authUser.getSalonId(), page, size, branchId, userId, clientId, date,
                            statusFilter);
                });
    }

    @Override
    public Mono<List<Appointment>> findByMonth(int year, int month, Long branchId, Long userId,
                                               AppointmentStatus status) {
        String statusFilter = status != null ? status.name() : null;
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime periodStart = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime periodEnd = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        return SecurityHelper.currentUser()
                .flatMap(authUser -> {
                    Mono<Long> countMono = SecurityHelper.isSuperAdmin(authUser)
                            ? appointmentRepositoryPort.countByPeriod(branchId, userId, statusFilter, periodStart,
                            periodEnd)
                            : appointmentRepositoryPort.countBySalonIdAndPeriod(authUser.getSalonId(), branchId,
                            userId, statusFilter, periodStart, periodEnd);

                    return countMono.flatMap(count -> {
                        if (count > MAX_CALENDAR_APPOINTMENTS) {
                            return Mono.error(new BadRequestException(
                                    "Too many appointments in this period. Please filter by branch or professional."));
                        }
                        Flux<Appointment> appointmentsFlux = SecurityHelper.isSuperAdmin(authUser)
                                ? appointmentRepositoryPort.findByPeriod(branchId, userId, statusFilter, periodStart,
                                periodEnd, MAX_CALENDAR_APPOINTMENTS)
                                : appointmentRepositoryPort.findBySalonIdAndPeriod(authUser.getSalonId(), branchId,
                                userId, statusFilter, periodStart, periodEnd, MAX_CALENDAR_APPOINTMENTS);

                        return appointmentsFlux
                                .flatMap(appointmentEnricher::enrich)
                                .collectList();
                    });
                });
    }

    @Override
    public Mono<Appointment> findById(Long id) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> loadWithTenantCheck(id, authUser)
                        .flatMap(appointmentEnricher::enrich));
    }

    @Override
    public Mono<Appointment> update(Long id, Appointment appointment) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> loadWithTenantCheck(id, authUser)
                        .flatMap(existing -> {
                            Long userId = appointment.getUserId() != null ? appointment.getUserId() : existing.getUserId();
                            Long branchId = appointment.getBranchId() != null ? appointment.getBranchId() : existing.getBranchId();
                            Long serviceId = appointment.getServiceId() != null ? appointment.getServiceId() : existing.getServiceId();
                            LocalDateTime startAt = appointment.getStartAt() != null ? appointment.getStartAt() : existing.getStartAt();

                            return validateReferences(existing.getClientId(), userId, branchId, serviceId, authUser.getSalonId())
                                    .flatMap(salonId -> resolveEndAt(serviceId, startAt)
                                            .flatMap(endAt -> ensureNoOverlap(userId, startAt, endAt, id)
                                                    .then(Mono.defer(() -> {
                                                        existing.setUserId(userId);
                                                        existing.setBranchId(branchId);
                                                        existing.setServiceId(serviceId);
                                                        existing.setStartAt(startAt);
                                                        existing.setEndAt(endAt);
                                                        if (appointment.getNotes() != null) {
                                                            existing.setNotes(appointment.getNotes());
                                                        }
                                                        if (appointment.getStatus() != null) {
                                                            existing.setStatus(appointment.getStatus());
                                                            if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
                                                                existing.setCancelledAt(LocalDateTime.now());
                                                                existing.setCancellationReason(appointment.getCancellationReason());
                                                            }
                                                        }
                                                        return appointmentRepositoryPort.save(existing);
                                                    }))));
                        })
                        .flatMap(appointmentEnricher::enrich));
    }

    @Override
    public Mono<Void> delete(Long id) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> loadWithTenantCheck(id, authUser)
                        .flatMap(appointment -> appointmentRepositoryPort.deleteById(id)));
    }

    private Mono<Appointment> persistAppointment(Appointment appointment, Long authSalonId, Long excludeId) {
        if (appointment.getStatus() == null) {
            appointment.setStatus(AppointmentStatus.SCHEDULED);
        }
        return validateReferences(appointment.getClientId(), appointment.getUserId(), appointment.getBranchId(),
                appointment.getServiceId(), authSalonId)
                .flatMap(salonId -> {
                    appointment.setSalonId(salonId);
                    return resolveEndAt(appointment.getServiceId(), appointment.getStartAt())
                            .flatMap(endAt -> {
                                appointment.setEndAt(endAt);
                                return ensureNoOverlap(appointment.getUserId(), appointment.getStartAt(), endAt, excludeId)
                                        .then(appointmentRepositoryPort.save(appointment));
                            });
                })
                .flatMap(appointmentEnricher::enrich);
    }

    private Mono<Long> validateReferences(Long clientId, Long userId, Long branchId, Long serviceId, Long authSalonId) {
        return clientRepositoryPort.findById(clientId)
                .switchIfEmpty(Mono.error(NotFoundException.forResource("Client", clientId)))
                .flatMap(client -> {
                    if (authSalonId != null && !authSalonId.equals(client.getSalonId())) {
                        return Mono.error(NotFoundException.forResource("Client", clientId));
                    }
                    return Mono.just(client.getSalonId());
                })
                .flatMap(salonId -> userRepositoryPort.findById(userId)
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("User", userId)))
                        .flatMap(user -> {
                            if (!salonId.equals(user.getSalonId())) {
                                return Mono.error(new BadRequestException("User does not belong to the same salon as the client"));
                            }
                            return Mono.just(salonId);
                        }))
                .flatMap(salonId -> branchRepositoryPort.findById(branchId)
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("Branch", branchId)))
                        .flatMap(branch -> {
                            if (!salonId.equals(branch.getSalonId())) {
                                return Mono.error(new BadRequestException("Branch does not belong to the same salon as the client"));
                            }
                            return Mono.just(salonId);
                        }))
                .flatMap(salonId -> {
                    if (serviceId == null) {
                        return Mono.just(salonId);
                    }
                    return serviceRepositoryPort.findById(serviceId)
                            .switchIfEmpty(Mono.error(NotFoundException.forResource("Service", serviceId)))
                            .flatMap(service -> {
                                if (!salonId.equals(service.getSalonId())) {
                                    return Mono.error(new BadRequestException("Service does not belong to the same salon as the client"));
                                }
                                return Mono.just(salonId);
                            });
                });
    }

    private Mono<LocalDateTime> resolveEndAt(Long serviceId, LocalDateTime startAt) {
        if (serviceId == null) {
            return Mono.just(startAt.plusMinutes(60));
        }
        return serviceRepositoryPort.findById(serviceId)
                .map(service -> {
                    int duration = service.getDurationMinutes() != null ? service.getDurationMinutes() : 60;
                    return startAt.plusMinutes(duration);
                });
    }

    private Mono<Void> ensureNoOverlap(Long userId, LocalDateTime startAt, LocalDateTime endAt, Long excludeId) {
        return appointmentRepositoryPort.countOverlapping(userId, startAt, endAt, excludeId)
                .flatMap(count -> {
                    if (count > 0) {
                        return Mono.error(new ConflictException("The professional already has an appointment in that time slot"));
                    }
                    return Mono.empty();
                });
    }

    private Mono<Appointment> loadWithTenantCheck(Long id, AuthUser authUser) {
        return appointmentRepositoryPort.findById(id)
                .switchIfEmpty(Mono.error(NotFoundException.forResource("Appointment", id)))
                .flatMap(appointment -> SecurityHelper.requireSameSalon(appointment, appointment.getSalonId(), authUser,
                        "Appointment", id));
    }

    private Mono<PageResponse<Appointment>> paginateAll(int page, int size, Long branchId, Long userId, Long clientId,
                                                        LocalDate date, String status) {
        return Mono.zip(
                appointmentRepositoryPort.countAll(branchId, userId, clientId, date, status),
                appointmentRepositoryPort.findAll(page, size, branchId, userId, clientId, date, status)
                        .flatMap(appointmentEnricher::enrich)
                        .collectList()
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }

    private Mono<PageResponse<Appointment>> paginateBySalonId(Long salonId, int page, int size, Long branchId,
                                                                Long userId, Long clientId, LocalDate date,
                                                                String status) {
        return Mono.zip(
                appointmentRepositoryPort.countBySalonId(salonId, branchId, userId, clientId, date, status),
                appointmentRepositoryPort.findBySalonId(salonId, page, size, branchId, userId, clientId, date, status)
                        .flatMap(appointmentEnricher::enrich)
                        .collectList()
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }
}
