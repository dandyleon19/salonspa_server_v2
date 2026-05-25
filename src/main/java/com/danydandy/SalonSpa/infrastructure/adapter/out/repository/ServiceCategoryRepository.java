package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.ServiceCategoryEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface ServiceCategoryRepository extends R2dbcRepository<ServiceCategoryEntity, Long> {
    Flux<ServiceCategoryEntity> findBySalonIdOrderByCreatedAtAsc(Long salonId);
}
