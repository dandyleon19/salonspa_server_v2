package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.domain.model.AuthUser;
import com.danydandy.SalonSpa.domain.model.Branch;
import com.danydandy.SalonSpa.domain.ports.in.BranchUseCase;
import com.danydandy.SalonSpa.domain.ports.out.BranchRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BranchService implements BranchUseCase {

    private final BranchRepositoryPort branchRepositoryPort;


    @Override
    public Mono<Branch> create(Branch branch) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (AuthUser) ctx.getAuthentication().getPrincipal())
                .flatMap(authUser -> {
                    branch.setSalonId(authUser.getSalonId());
                    return branchRepositoryPort.save(branch);
                });
    }

    @Override
    public Flux<Branch> findAll() {
        return branchRepositoryPort.findAll();
    }

    @Override
    public Mono<Branch> findById(Long id) {
        return branchRepositoryPort.findById(id);
    }

    @Override
    public Mono<Branch> update(Long id, Branch branch) {
        return branchRepositoryPort.findById(id)
                .flatMap(existing -> {
                    existing.setName(branch.getName());
                    existing.setCity(branch.getCity());
                    existing.setAddress(branch.getAddress());
                    return branchRepositoryPort.save(existing);
                });
    }

    @Override
    public Mono<Void> delete(Long id) {
        return branchRepositoryPort.deleteById(id);
    }

    @Override
    public Flux<Branch> findBySalonId() {
        // return branchRepositoryPort.findBySalonId(id);
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (AuthUser) ctx.getAuthentication().getPrincipal())
                .flatMapMany(authUser ->
                        branchRepositoryPort.findBySalonId(authUser.getSalonId())
                );
    }
}
