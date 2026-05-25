package com.danydandy.SalonSpa.domain.ports.in;

import com.danydandy.SalonSpa.domain.model.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ServiceUseCase {
    Mono<Service> create(Service service);
    Flux<Service> findAll();
    Mono<Service> findById(Long id);
    Mono<Service> update(Long id, Service service);
    Mono<Void> delete(Long id);
    Flux<Service> findBySalonId();
}
