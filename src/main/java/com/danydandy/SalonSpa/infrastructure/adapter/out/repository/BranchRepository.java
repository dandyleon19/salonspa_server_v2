package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.BranchEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchRepository extends R2dbcRepository<BranchEntity, Long> {
    Flux<BranchEntity> findBySalonIdOrderByCreatedAtAsc(Long salondId);

    Flux<BranchEntity> findAllByOrderByCreatedAtAsc(Pageable pageable);

    Mono<Long> countBySalonId(Long salonId);
}
