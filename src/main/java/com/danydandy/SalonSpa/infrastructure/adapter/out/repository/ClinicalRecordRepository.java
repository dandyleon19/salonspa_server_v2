package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.ClinicalRecordEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface ClinicalRecordRepository extends R2dbcRepository<ClinicalRecordEntity, Long> {
    Flux<ClinicalRecordEntity> findByClientId(Long clientId);
}
