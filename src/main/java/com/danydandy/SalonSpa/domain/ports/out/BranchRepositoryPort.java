package com.danydandy.SalonSpa.domain.ports.out;

import com.danydandy.SalonSpa.domain.model.Branch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchRepositoryPort {
    Mono<Branch> save(Branch salon);
    Flux<Branch> findAll(int page, int size, String search);
    Mono<Long> countAll(String search);
    Mono<Branch> findById(Long id);
    Mono<Void> deleteById(Long id);
    Flux<Branch> findBySalonId(Long salonId, int page, int size, String search);
    Mono<Long> countBySalonId(Long salonId, String search);
}
