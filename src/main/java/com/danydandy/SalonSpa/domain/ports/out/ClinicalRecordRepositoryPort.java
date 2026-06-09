package com.danydandy.SalonSpa.domain.ports.out;

import com.danydandy.SalonSpa.domain.model.ClinicalRecord;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClinicalRecordRepositoryPort {
    Mono<ClinicalRecord> save(ClinicalRecord clinicalRecord);
    Flux<ClinicalRecord> findAll(int page, int size);
    Mono<Long> countAll();
    Mono<ClinicalRecord> findById(Long id);
    Mono<Void> deleteById(Long id);
    Flux<ClinicalRecord> findByClientId(Long clientId, int page, int size);
    Mono<Long> countByClientId(Long clientId);
    Flux<ClinicalRecord> findBySalonId(Long salonId, int page, int size);
    Mono<Long> countBySalonId(Long salonId);
}
