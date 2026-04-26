package com.danydandy.SalonSpa.domain.ports.out;

import com.danydandy.SalonSpa.domain.model.Salon;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SalonRepositoryPort {
    Mono<Salon> save(Salon salon);
    Flux<Salon> findAll();
    Mono<Salon> findById(Long id);
    Mono<Void> deleteById(Long id);
}
