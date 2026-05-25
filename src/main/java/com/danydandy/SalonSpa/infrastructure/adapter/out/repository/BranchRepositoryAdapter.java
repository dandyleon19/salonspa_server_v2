package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.domain.model.Branch;
import com.danydandy.SalonSpa.domain.ports.out.BranchRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.BranchEntity;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.BranchMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BranchRepositoryAdapter implements BranchRepositoryPort {

    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;

    @Override
    public Mono<Branch> save(Branch salon) {
        return branchRepository.save(branchMapper.toEntity(salon))
                .map(branchMapper::toDomain);
    }

    @Override
    public Flux<Branch> findAll() {
        return branchRepository.findAll()
                .map(branchMapper::toDomain);
    }

    @Override
    public Mono<Branch> findById(Long id) {
        return branchRepository.findById(id)
                .map(branchMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return branchRepository.deleteById(id);
    }

    @Override
    public Flux<Branch> findBySalonId(Long id) {
        return branchRepository.findBySalonIdOrderByCreatedAtAsc(id)
                .map(branchMapper::toDomain);
    }
}
