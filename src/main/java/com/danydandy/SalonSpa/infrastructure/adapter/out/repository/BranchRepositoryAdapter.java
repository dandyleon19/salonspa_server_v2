package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.domain.model.Branch;
import com.danydandy.SalonSpa.domain.ports.out.BranchRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.BranchEntity;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BranchRepositoryAdapter implements BranchRepositoryPort {

    private final BranchRepository branchRepository;

    private Branch toDomain(BranchEntity entity) {
        return Branch.builder()
                .id(entity.getId())
                .name(entity.getName())
                .address(entity.getAddress())
                .city(entity.getCity())
                .salonId(entity.getSalonId())
                .build();
    }

    private BranchEntity toEntity(Branch branch) {
        return BranchEntity.builder()
                .id(branch.getId())
                .name(branch.getName())
                .address(branch.getAddress())
                .city(branch.getCity())
                .salonId(branch.getSalonId())
                .build();
    }

    @Override
    public Mono<Branch> save(Branch salon) {
        return branchRepository.save(toEntity(salon))
                .map(this::toDomain);
    }

    @Override
    public Flux<Branch> findAll() {
        return branchRepository.findAll()
                .map(this::toDomain);
    }

    @Override
    public Mono<Branch> findById(Long id) {
        return branchRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return branchRepository.deleteById(id);
    }

    @Override
    public Flux<Branch> findBySalonId(Long id) {
        return branchRepository.findBySalonId(id)
                .map(this::toDomain);
    }
}
