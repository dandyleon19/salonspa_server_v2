package com.danydandy.SalonSpa.domain.ports.out;

import com.danydandy.SalonSpa.domain.model.Client;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClientRepositoryPort {
    Mono<Client> save(Client client);
    Flux<Client> findAll(int page, int size);
    Mono<Long> countAll();
    Mono<Client> findById(Long id);
    Mono<Void> deleteById(Long id);
    Flux<Client> findBySalonId(Long salonId, int page, int size);
    Mono<Long> countBySalonId(Long salonId);
}
