package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.ClinicalRecordServiceEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface ClinicalRecordServiceRepository extends R2dbcRepository<ClinicalRecordServiceEntity, Long> {
    Flux<ClinicalRecordServiceEntity> findByClinicalRecordId(Long clinicalRecordId);
}
