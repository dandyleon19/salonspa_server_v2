package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.ClientEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface ClientRepository extends R2dbcRepository<ClientEntity, Long> {
    Flux<ClientEntity> findBySalonId(Long salondId);
}
