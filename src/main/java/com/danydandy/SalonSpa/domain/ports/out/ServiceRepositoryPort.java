package com.danydandy.SalonSpa.domain.ports.out;

import com.danydandy.SalonSpa.domain.model.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ServiceRepositoryPort {
    Mono<Service> save(Service service);
    Flux<Service> findAll(int page, int size, String search);
    Mono<Long> countAll(String search);
    Mono<Service> findById(Long id);
    Mono<Void> deleteById(Long id);
    Flux<Service> findBySalonId(Long salonId, int page, int size, String search);
    Mono<Long> countBySalonId(Long salonId, String search);
    Flux<Service> findByCategoryId(Long id);
}
