package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.domain.model.AuthUser;
import com.danydandy.SalonSpa.domain.model.Branch;
import com.danydandy.SalonSpa.domain.model.Client;
import com.danydandy.SalonSpa.domain.ports.in.BranchUseCase;
import com.danydandy.SalonSpa.domain.ports.out.BranchRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.SalonRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BranchServiceImpl implements BranchUseCase {

    private final BranchRepositoryPort branchRepositoryPort;
    private final SalonRepositoryPort salonRepositoryPort;

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
    public Mono<PageResponse<Branch>> findPage(int page, int size) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (AuthUser) ctx.getAuthentication().getPrincipal())
                .flatMap(authUser -> {
                    if ("SUPER_ADMIN".equals(authUser.getRole())) {
                        return paginateAll(page, size);
                    }
                    return paginateBySalonId(authUser.getSalonId(), page, size);
                });
    }

    @Override
    public Mono<Branch> findById(Long id) {
        return branchRepositoryPort.findById(id)
                .flatMap(branch -> salonRepositoryPort.findById(branch.getSalonId())
                        .map(salon -> {
                            branch.setSalon(salon);
                            return branch;
                        }));
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
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (AuthUser) ctx.getAuthentication().getPrincipal())
                .flatMapMany(authUser ->
                        branchRepositoryPort.findBySalonId(authUser.getSalonId(), 0, 100)
                                .flatMap(branch -> salonRepositoryPort.findById(branch.getSalonId())
                                        .map(salon -> {
                                            branch.setSalon(salon);
                                            return branch;
                                        }))
                );
    }

    private Mono<PageResponse<Branch>> paginateAll(int page, int size) {
        return Mono.zip(
                branchRepositoryPort.countAll(),
                branchRepositoryPort.findAll(page, size)
                        .flatMap(branch -> salonRepositoryPort.findById(branch.getSalonId())
                                .map(salon -> {
                                    branch.setSalon(salon);
                                    return branch;
                                }))
                        .collectList()
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }

    private Mono<PageResponse<Branch>> paginateBySalonId(Long salonId, int page, int size) {
        return Mono.zip(
                branchRepositoryPort.countBySalonId(salonId),
                branchRepositoryPort.findBySalonId(salonId, page, size)
                        .flatMap(branch -> salonRepositoryPort.findById(branch.getSalonId())
                                .map(salon -> {
                                    branch.setSalon(salon);
                                    return branch;
                                }))
                        .collectList()
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }
}
