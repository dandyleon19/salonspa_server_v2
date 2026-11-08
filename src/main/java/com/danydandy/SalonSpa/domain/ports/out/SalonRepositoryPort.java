package com.danydandy.SalonSpa.domain.ports.out;

import com.danydandy.SalonSpa.domain.model.Salon;
import com.danydandy.SalonSpa.domain.model.SalonLastSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

public interface SalonRepositoryPort {
    Mono<Salon> save(Salon salon);
    Flux<Salon> findAll(int page, int size, String search);
    Mono<Long> countAll(String search);
    Mono<Salon> findById(Long id);
    Mono<Void> deleteById(Long id);
    /** The most recent session of each of the given salons (salons that never had one are absent). */
    Flux<SalonLastSession> findLastSessions(Collection<Long> salonIds);
}
