package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.ClinicalRecordEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClinicalRecordRepository extends R2dbcRepository<ClinicalRecordEntity, Long> {

    Flux<ClinicalRecordEntity> findByClientIdOrderByCreatedAtAsc(Long clientId, Pageable pageable);

    Mono<Long> countByClientId(Long clientId);

    Flux<ClinicalRecordEntity> findAllByOrderByCreatedAtAsc(Pageable pageable);

    @Query("""
            SELECT cr.id, cr.diagnosis, cr.treatment, cr.observations, cr.session_date,
                   cr.client_id, cr.user_id, cr.branch_id, cr.created_at, cr.updated_at
            FROM clinical_records cr
            INNER JOIN clients c ON cr.client_id = c.id
            WHERE c.salon_id = :salonId
            ORDER BY cr.created_at ASC
            LIMIT :limit OFFSET :offset
            """)
    Flux<ClinicalRecordEntity> findPageBySalonId(Long salonId, int limit, long offset);

    @Query("""
            SELECT COUNT(*)
            FROM clinical_records cr
            INNER JOIN clients c ON cr.client_id = c.id
            WHERE c.salon_id = :salonId
            """)
    Mono<Long> countBySalonId(Long salonId);
}
