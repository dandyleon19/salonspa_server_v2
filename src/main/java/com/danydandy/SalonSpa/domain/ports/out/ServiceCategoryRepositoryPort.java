package com.danydandy.SalonSpa.domain.ports.out;

import com.danydandy.SalonSpa.domain.model.ServiceCategory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ServiceCategoryRepositoryPort {
    Mono<ServiceCategory> save(ServiceCategory serviceCategory);
    Flux<ServiceCategory> findAll();
    Mono<ServiceCategory> findById(Long id);
    Mono<Void> deleteById(Long id);
    Flux<ServiceCategory> findBySalonId(Long id);
}
