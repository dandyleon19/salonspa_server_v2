package com.danydandy.SalonSpa.domain.ports.out;

import com.danydandy.SalonSpa.domain.model.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ServiceRepositoryPort {
    Mono<Service> save(Service service);
    Flux<Service> findAll(int page, int size);
    Mono<Long> countAll();
    Mono<Service> findById(Long id);
    Mono<Void> deleteById(Long id);
    Flux<Service> findBySalonId(Long salonId, int page, int size);
    Mono<Long> countBySalonId(Long salonId);
    Flux<Service> findByCategoryId(Long id);
}
