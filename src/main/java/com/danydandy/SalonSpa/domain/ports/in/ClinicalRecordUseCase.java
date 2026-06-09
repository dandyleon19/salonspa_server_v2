package com.danydandy.SalonSpa.domain.ports.in;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.domain.model.ClinicalRecord;
import reactor.core.publisher.Mono;

public interface ClinicalRecordUseCase {
    Mono<ClinicalRecord> create(ClinicalRecord clinicalRecord);
    Mono<PageResponse<ClinicalRecord>> findPage(int page, int size);
    Mono<ClinicalRecord> findById(Long id);
    Mono<ClinicalRecord> update(Long id, ClinicalRecord clinicalRecord);
    Mono<Void> delete(Long id);
}
