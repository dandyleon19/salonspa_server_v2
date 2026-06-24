package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.application.security.SecurityHelper;
import com.danydandy.SalonSpa.domain.exception.NotFoundException;
import com.danydandy.SalonSpa.domain.model.*;
import com.danydandy.SalonSpa.domain.ports.in.AppointmentUseCase;
import com.danydandy.SalonSpa.domain.ports.in.ClinicalRecordUseCase;
import com.danydandy.SalonSpa.domain.ports.out.*;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ClinicalRecordServiceImpl implements ClinicalRecordUseCase {

    private final ClinicalRecordRepositoryPort clinicalRecordRepositoryPort;
    private final ClinicalRecordServiceRepositoryPort clinicalRecordServiceRepositoryPort;
    private final ClientRepositoryPort clientRepositoryPort;
    private final AppointmentUseCase appointmentUseCase;
    private final UserRepositoryPort userRepositoryPort;
    private final BranchRepositoryPort branchRepositoryPort;
    private final ServiceRepositoryPort serviceRepositoryPort;

    @Override
    public Mono<ClinicalRecord> create(ClinicalRecord clinicalRecord) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> clientRepositoryPort.findById(clinicalRecord.getClientId())
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("Client", clinicalRecord.getClientId())))
                        .flatMap(client -> SecurityHelper.requireSameSalon(client, client.getSalonId(), authUser, "Client", clinicalRecord.getClientId()))
                        .then(clinicalRecordRepositoryPort.save(clinicalRecord))
                        .flatMap(savedClinicalRecord -> saveClinicalRecordService(clinicalRecord, savedClinicalRecord))
                        .flatMap(savedClinicalRecord -> handleNextAppointment(savedClinicalRecord, clinicalRecord.getNextAppointment(), null))
                        .flatMap(this::enrichClinicalRecord));
    }

    @Override
    public Mono<PageResponse<ClinicalRecord>> findPage(int page, int size) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> {
                    if (SecurityHelper.isSuperAdmin(authUser)) {
                        return paginateAll(page, size);
                    }
                    return paginateBySalonId(authUser.getSalonId(), page, size);
                });
    }

    @Override
    public Mono<ClinicalRecord> findById(Long id) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> loadWithTenantCheck(id, authUser)
                        .flatMap(this::enrichClinicalRecord));
    }

    @Override
    public Mono<ClinicalRecord> update(Long id, ClinicalRecord clinicalRecord) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> loadWithTenantCheck(id, authUser)
                        .flatMap(existing -> {
                            existing.setDiagnosis(clinicalRecord.getDiagnosis());
                            existing.setObservations(clinicalRecord.getObservations());
                            existing.setTreatment(clinicalRecord.getTreatment());
                            existing.setSessionDate(clinicalRecord.getSessionDate());
                            return clinicalRecordRepositoryPort.save(existing)
                                    .flatMap(saved -> handleNextAppointment(saved, clinicalRecord.getNextAppointment(), saved.getFollowUpAppointmentId()))
                                    .flatMap(this::enrichClinicalRecord);
                        }));
    }

    @Override
    public Mono<Void> delete(Long id) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> loadWithTenantCheck(id, authUser)
                        .flatMap(record -> clinicalRecordRepositoryPort.deleteById(id)));
    }

    private Mono<ClinicalRecord> saveClinicalRecordService(ClinicalRecord clinicalRecord, ClinicalRecord savedClinicalRecord) {
        Long serviceId = clinicalRecord.getServiceId();
        if (serviceId == null) {
            return Mono.just(savedClinicalRecord);
        }
        ClinicalRecordService clinicalRecordService = new ClinicalRecordService();
        clinicalRecordService.setClinicalRecordId(savedClinicalRecord.getId());
        clinicalRecordService.setServiceId(serviceId);
        return clinicalRecordServiceRepositoryPort.save(clinicalRecordService)
                .thenReturn(savedClinicalRecord);
    }

    private Mono<ClinicalRecord> handleNextAppointment(ClinicalRecord savedRecord, Appointment nextAppointment,
                                                       Long existingFollowUpId) {
        if (nextAppointment == null) {
            return Mono.just(savedRecord);
        }

        nextAppointment.setClientId(savedRecord.getClientId());
        if (nextAppointment.getServiceId() == null) {
            nextAppointment.setServiceId(savedRecord.getServiceId());
        }

        if (existingFollowUpId != null) {
            return appointmentUseCase.update(existingFollowUpId, nextAppointment)
                    .flatMap(updated -> {
                        savedRecord.setFollowUpAppointmentId(updated.getId());
                        savedRecord.setNextAppointment(updated);
                        return clinicalRecordRepositoryPort.save(savedRecord);
                    });
        }

        return appointmentUseCase.scheduleFollowUp(nextAppointment)
                .flatMap(created -> {
                    savedRecord.setFollowUpAppointmentId(created.getId());
                    savedRecord.setNextAppointment(created);
                    return clinicalRecordRepositoryPort.save(savedRecord);
                });
    }

    private Mono<ClinicalRecord> enrichClinicalRecord(ClinicalRecord clinicalRecord) {
        Mono<java.util.List<String>> servicesMono = clinicalRecordServiceRepositoryPort
                .findByClinicalRecordId(clinicalRecord.getId())
                .flatMap(clinicalRecordService -> serviceRepositoryPort
                        .findById(clinicalRecordService.getServiceId()))
                .map(Service::getName)
                .collectList();

        Mono<String> userNameMono = clinicalRecord.getUserId() != null
                ? userRepositoryPort.findById(clinicalRecord.getUserId())
                        .map(user -> user.getFirstName() + " " + user.getLastName())
                : Mono.just("");

        Mono<String> branchNameMono = clinicalRecord.getBranchId() != null
                ? branchRepositoryPort.findById(clinicalRecord.getBranchId()).map(Branch::getName)
                : Mono.just("");

        return Mono.zip(userNameMono, branchNameMono, servicesMono)
                .flatMap(tuple -> {
                    clinicalRecord.setUserName(tuple.getT1());
                    clinicalRecord.setBranchName(tuple.getT2());
                    clinicalRecord.setAssociatedServices(tuple.getT3());
                    if (clinicalRecord.getNextAppointment() != null) {
                        return Mono.just(clinicalRecord);
                    }
                    if (clinicalRecord.getFollowUpAppointmentId() == null) {
                        return Mono.just(clinicalRecord);
                    }
                    return appointmentUseCase.findById(clinicalRecord.getFollowUpAppointmentId())
                            .map(appointment -> {
                                clinicalRecord.setNextAppointment(appointment);
                                return clinicalRecord;
                            });
                });
    }

    private Mono<ClinicalRecord> loadWithTenantCheck(Long id, AuthUser authUser) {
        return clinicalRecordRepositoryPort.findById(id)
                .switchIfEmpty(Mono.error(NotFoundException.forResource("ClinicalRecord", id)))
                .flatMap(record -> clientRepositoryPort.findById(record.getClientId())
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("ClinicalRecord", id)))
                        .flatMap(client -> SecurityHelper.requireSameSalon(record, client.getSalonId(), authUser, "ClinicalRecord", id)));
    }

    private Mono<PageResponse<ClinicalRecord>> paginateAll(int page, int size) {
        return Mono.zip(
                clinicalRecordRepositoryPort.countAll(),
                clinicalRecordRepositoryPort.findAll(page, size)
                        .flatMap(this::enrichClinicalRecord)
                        .collectList()
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }

    private Mono<PageResponse<ClinicalRecord>> paginateBySalonId(Long salonId, int page, int size) {
        return Mono.zip(
                clinicalRecordRepositoryPort.countBySalonId(salonId),
                clinicalRecordRepositoryPort.findBySalonId(salonId, page, size)
                        .flatMap(this::enrichClinicalRecord)
                        .collectList()
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }
}
