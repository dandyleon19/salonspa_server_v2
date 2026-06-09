package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.ClientEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClientRepository extends R2dbcRepository<ClientEntity, Long> {
    Flux<ClientEntity> findBySalonIdOrderByCreatedAtAsc(Long salonId, Pageable pageable);

    Flux<ClientEntity> findAllByOrderByCreatedAtAsc(Pageable pageable);

    Mono<Long> countBySalonId(Long salonId);
}
