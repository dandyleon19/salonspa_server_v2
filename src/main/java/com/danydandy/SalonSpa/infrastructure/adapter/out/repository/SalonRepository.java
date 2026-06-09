package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.SalonEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface SalonRepository extends R2dbcRepository<SalonEntity, Long> {
    Flux<SalonEntity> findAllByOrderByCreatedAtAsc(Pageable pageable);
}
