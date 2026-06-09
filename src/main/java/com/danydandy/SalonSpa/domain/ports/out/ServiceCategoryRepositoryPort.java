package com.danydandy.SalonSpa.domain.ports.out;

import com.danydandy.SalonSpa.domain.model.ServiceCategory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ServiceCategoryRepositoryPort {
    Mono<ServiceCategory> save(ServiceCategory serviceCategory);
    Flux<ServiceCategory> findAll(int page, int size);
    Mono<Long> countAll();
    Mono<ServiceCategory> findById(Long id);
    Mono<Void> deleteById(Long id);
    Flux<ServiceCategory> findBySalonId(Long salonId, int page, int size);
    Mono<Long> countBySalonId(Long salonId);
}
