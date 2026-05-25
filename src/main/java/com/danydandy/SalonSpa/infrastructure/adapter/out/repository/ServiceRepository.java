package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.ServiceEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface ServiceRepository extends R2dbcRepository<ServiceEntity, Long> {
    Flux<ServiceEntity> findByCategoryIdOrderByCreatedAtAsc(Long categoryId);
    Flux<ServiceEntity> findBySalonIdOrderByCreatedAtAsc(Long salonId);
}
