package com.danydandy.SalonSpa.domain.ports.in;

import com.danydandy.SalonSpa.domain.model.ServiceCategory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ServiceCategoryUseCase {
    Mono<ServiceCategory> create(ServiceCategory serviceCategory);
    Flux<ServiceCategory> findAll();
    Mono<ServiceCategory> findById(Long id);
    Mono<ServiceCategory> update(Long id, ServiceCategory serviceCategory);
    Mono<Void> delete(Long id);
    Flux<ServiceCategory> findBySalonId();
}
