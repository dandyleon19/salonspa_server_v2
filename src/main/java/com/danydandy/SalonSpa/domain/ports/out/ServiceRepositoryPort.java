package com.danydandy.SalonSpa.domain.ports.out;

import com.danydandy.SalonSpa.domain.model.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ServiceRepositoryPort {
    Mono<Service> save(Service service);
    Flux<Service> findAll();
    Mono<Service> findById(Long id);
    Mono<Void> deleteById(Long id);
    Flux<Service> findBySalonId(Long id);
    Flux<Service> findByCategoryId(Long id);
}
