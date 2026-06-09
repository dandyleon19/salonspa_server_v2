package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.application.security.SecurityHelper;
import com.danydandy.SalonSpa.domain.exception.NotFoundException;
import com.danydandy.SalonSpa.domain.model.AuthUser;
import com.danydandy.SalonSpa.domain.model.ClinicalRecord;
import com.danydandy.SalonSpa.domain.model.ClinicalRecordService;
import com.danydandy.SalonSpa.domain.ports.in.ClinicalRecordUseCase;
import com.danydandy.SalonSpa.domain.ports.out.ClientRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.ClinicalRecordRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.ClinicalRecordServiceRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ClinicalRecordServiceImpl implements ClinicalRecordUseCase {

    private final ClinicalRecordRepositoryPort clinicalRecordRepositoryPort;
    private final ClinicalRecordServiceRepositoryPort clinicalRecordServiceRepositoryPort;
    private final ClientRepositoryPort clientRepositoryPort;

    @Override
    public Mono<ClinicalRecord> create(ClinicalRecord clinicalRecord) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> clientRepositoryPort.findById(clinicalRecord.getClientId())
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("Client", clinicalRecord.getClientId())))
                        .flatMap(client -> SecurityHelper.requireSameSalon(client, client.getSalonId(), authUser, "Client", clinicalRecord.getClientId()))
                        .then(clinicalRecordRepositoryPort.save(clinicalRecord))
                        .flatMap(savedClinicalRecord -> {
                            Long serviceId = clinicalRecord.getServiceId();

                            if (serviceId == null) {
                                return Mono.just(savedClinicalRecord);
                            }

                            ClinicalRecordService clinicalRecordService = new ClinicalRecordService();
                            clinicalRecordService.setClinicalRecordId(savedClinicalRecord.getId());
                            clinicalRecordService.setServiceId(clinicalRecord.getServiceId());
                            return clinicalRecordServiceRepositoryPort.save(clinicalRecordService)
                                    .thenReturn(savedClinicalRecord);
                        }));
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
                .flatMap(authUser -> loadWithTenantCheck(id, authUser));
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
                            return clinicalRecordRepositoryPort.save(existing);
                        }));
    }

    @Override
    public Mono<Void> delete(Long id) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> loadWithTenantCheck(id, authUser)
                        .flatMap(record -> clinicalRecordRepositoryPort.deleteById(id)));
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
                clinicalRecordRepositoryPort.findAll(page, size).collectList()
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }

    private Mono<PageResponse<ClinicalRecord>> paginateBySalonId(Long salonId, int page, int size) {
        return Mono.zip(
                clinicalRecordRepositoryPort.countBySalonId(salonId),
                clinicalRecordRepositoryPort.findBySalonId(salonId, page, size).collectList()
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }
}
