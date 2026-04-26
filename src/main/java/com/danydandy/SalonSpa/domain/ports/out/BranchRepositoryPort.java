package com.danydandy.SalonSpa.domain.ports.out;

import com.danydandy.SalonSpa.domain.model.Branch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchRepositoryPort {
    Mono<Branch> save(Branch salon);
    Flux<Branch> findAll();
    Mono<Branch> findById(Long id);
    Mono<Void> deleteById(Long id);
    Flux<Branch> findBySalonId(Long id);
}
