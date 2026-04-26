package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.BranchEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface BranchRepository extends R2dbcRepository<BranchEntity, Long> {
    Flux<BranchEntity> findBySalonId(Long salondId);
}
