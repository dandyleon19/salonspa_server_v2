package com.danydandy.SalonSpa.domain.ports.out;

import com.danydandy.SalonSpa.domain.model.ClinicalRecordService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClinicalRecordServiceRepositoryPort {
    Mono<ClinicalRecordService> save(ClinicalRecordService clinicalRecordService);
    Mono<Void> deleteById(Long id);
    Flux<ClinicalRecordService> findByClinicalRecordId(Long id);
}
