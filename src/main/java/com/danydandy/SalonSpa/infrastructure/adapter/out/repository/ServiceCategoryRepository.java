package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.ServiceCategoryEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ServiceCategoryRepository extends R2dbcRepository<ServiceCategoryEntity, Long> {

    Flux<ServiceCategoryEntity> findAllByOrderByCreatedAtAsc(Pageable pageable);

    Flux<ServiceCategoryEntity> findBySalonIdOrderByCreatedAtAsc(Long salonId, Pageable pageable);

    Mono<Long> countBySalonId(Long salonId);
}
