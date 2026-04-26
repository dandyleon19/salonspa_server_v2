package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.domain.model.ClinicalRecord;
import com.danydandy.SalonSpa.domain.ports.in.ClinicalRecordUseCase;
import com.danydandy.SalonSpa.domain.ports.out.ClinicalRecordRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ClinicalRecordService implements ClinicalRecordUseCase {

    private final ClinicalRecordRepositoryPort clinicalRecordRepositoryPort;

    @Override
    public Mono<ClinicalRecord> create(ClinicalRecord clinicalRecord) {
        return clinicalRecordRepositoryPort.save(clinicalRecord);
    }

    @Override
    public Flux<ClinicalRecord> findAll() {
        return clinicalRecordRepositoryPort.findAll();
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
}
