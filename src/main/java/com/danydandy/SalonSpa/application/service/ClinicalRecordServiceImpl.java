package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.domain.model.AuthUser;
import com.danydandy.SalonSpa.domain.model.ClinicalRecord;
import com.danydandy.SalonSpa.domain.model.ClinicalRecordService;
import com.danydandy.SalonSpa.domain.ports.in.ClinicalRecordUseCase;
import com.danydandy.SalonSpa.domain.ports.out.ClinicalRecordRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.ClinicalRecordServiceRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ClinicalRecordServiceImpl implements ClinicalRecordUseCase {

    private final ClinicalRecordRepositoryPort clinicalRecordRepositoryPort;
    private final ClinicalRecordServiceRepositoryPort clinicalRecordServiceRepositoryPort;

    @Override
    public Mono<ClinicalRecord> create(ClinicalRecord clinicalRecord) {
        return clinicalRecordRepositoryPort.save(clinicalRecord)
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
                });
    }

    @Override
    public Mono<PageResponse<ClinicalRecord>> findPage(int page, int size) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (AuthUser) ctx.getAuthentication().getPrincipal())
                .flatMap(authUser -> {
                    if ("SUPER_ADMIN".equals(authUser.getRole())) {
                        return paginateAll(page, size);
                    }
                    return paginateBySalonId(authUser.getSalonId(), page, size);
                });
    }

    @Override
    public Mono<ClinicalRecord> findById(Long id) {
        return clinicalRecordRepositoryPort.findById(id);
    }

    @Override
    public Mono<ClinicalRecord> update(Long id, ClinicalRecord clinicalRecord) {
        return clinicalRecordRepositoryPort.findById(id)
                .flatMap(existing -> {
                    existing.setDiagnosis(clinicalRecord.getDiagnosis());
                    existing.setObservations(clinicalRecord.getObservations());
                    existing.setTreatment(clinicalRecord.getTreatment());
                    existing.setSessionDate(clinicalRecord.getSessionDate());
                    return clinicalRecordRepositoryPort.save(existing);
                });
    }

    @Override
    public Mono<Void> delete(Long id) {
        return clinicalRecordRepositoryPort.deleteById(id);
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
