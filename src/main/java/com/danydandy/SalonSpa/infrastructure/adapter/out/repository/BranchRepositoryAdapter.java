package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.domain.model.Branch;
import com.danydandy.SalonSpa.domain.ports.out.BranchRepositoryPort;
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
    public Flux<Branch> findAll(int page, int size, String search) {
        long offset = (long) page * size;
        return branchRepository.findPage(search, size, offset)
                .map(branchMapper::toDomain);
    }

    @Override
    public Mono<Long> countAll(String search) {
        return branchRepository.countFiltered(search);
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
    public Flux<Branch> findBySalonId(Long salonId, int page, int size, String search) {
        long offset = (long) page * size;
        return branchRepository.findPageBySalonId(salonId, search, size, offset)
                .map(branchMapper::toDomain);
    }

    @Override
    public Mono<Long> countBySalonId(Long salonId, String search) {
        return branchRepository.countBySalonId(salonId, search);
    }
}
