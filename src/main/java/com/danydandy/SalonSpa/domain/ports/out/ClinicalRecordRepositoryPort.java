package com.danydandy.SalonSpa.domain.ports.out;

import com.danydandy.SalonSpa.domain.model.ClinicalRecord;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClinicalRecordRepositoryPort {
    Mono<ClinicalRecord> save(ClinicalRecord clinicalRecord);
    Flux<ClinicalRecord> findAll();
    Mono<ClinicalRecord> findById(Long id);
    Mono<Void> deleteById(Long id);
    Flux<ClinicalRecord> findByClientId(Long id);
}
