package com.danydandy.SalonSpa.domain.ports.in;

import com.danydandy.SalonSpa.domain.model.Client;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClientUseCase {
    Mono<Client> create(Client client);
    Flux<Client> findAll();
    Mono<Client> findById(Long id);
    Mono<Client> update(Long id, Client client);
    Mono<Void> delete(Long id);
    Flux<Client> findBySalonId(Long id);
}
