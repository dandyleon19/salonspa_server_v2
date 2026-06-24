package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.application.security.SecurityHelper;
import com.danydandy.SalonSpa.application.util.SearchHelper;
import com.danydandy.SalonSpa.domain.exception.NotFoundException;
import com.danydandy.SalonSpa.domain.model.Branch;
import com.danydandy.SalonSpa.domain.ports.in.BranchUseCase;
import com.danydandy.SalonSpa.domain.ports.out.BranchRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.SalonRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BranchServiceImpl implements BranchUseCase {

    private final BranchRepositoryPort branchRepositoryPort;
    private final SalonRepositoryPort salonRepositoryPort;

    @Override
    public Mono<Branch> create(Branch branch) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> {
                    branch.setSalonId(authUser.getSalonId());
                    return branchRepositoryPort.save(branch);
                });
    }

    @Override
    public Mono<PageResponse<Branch>> findPage(int page, int size, String search) {
        String searchFilter = SearchHelper.toLikePattern(search);
        return SecurityHelper.currentUser()
                .flatMap(authUser -> {
                    if (SecurityHelper.isSuperAdmin(authUser)) {
                        return paginateAll(page, size, searchFilter);
                    }
                    return paginateBySalonId(authUser.getSalonId(), page, size, searchFilter);
                });
    }

    @Override
    public Mono<Branch> findById(Long id) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> branchRepositoryPort.findById(id)
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("Branch", id)))
                        .flatMap(branch -> SecurityHelper.requireSameSalon(branch, branch.getSalonId(), authUser, "Branch", id))
                        .flatMap(this::enrichWithSalon));
    }

    @Override
    public Mono<Branch> update(Long id, Branch branch) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> branchRepositoryPort.findById(id)
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("Branch", id)))
                        .flatMap(existing -> SecurityHelper.requireSameSalon(existing, existing.getSalonId(), authUser, "Branch", id))
                        .flatMap(existing -> {
                            existing.setName(branch.getName());
                            existing.setCity(branch.getCity());
                            existing.setAddress(branch.getAddress());
                            return branchRepositoryPort.save(existing);
                        }));
    }

    @Override
    public Mono<Void> delete(Long id) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> branchRepositoryPort.findById(id)
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("Branch", id)))
                        .flatMap(branch -> SecurityHelper.requireSameSalon(branch, branch.getSalonId(), authUser, "Branch", id))
                        .flatMap(branch -> branchRepositoryPort.deleteById(id)));
    }

    @Override
    public Flux<Branch> findBySalonId() {
        return SecurityHelper.currentUser()
                .flatMapMany(authUser ->
                        branchRepositoryPort.findBySalonId(authUser.getSalonId(), 0, 100, null)
                                .flatMap(this::enrichWithSalon)
                );
    }

    private Mono<Branch> enrichWithSalon(Branch branch) {
        return salonRepositoryPort.findById(branch.getSalonId())
                .map(salon -> {
                    branch.setSalon(salon);
                    return branch;
                });
    }

    private Mono<PageResponse<Branch>> paginateAll(int page, int size, String search) {
        return Mono.zip(
                branchRepositoryPort.countAll(search),
                branchRepositoryPort.findAll(page, size, search)
                        .flatMap(this::enrichWithSalon)
                        .collectList()
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }

    private Mono<PageResponse<Branch>> paginateBySalonId(Long salonId, int page, int size, String search) {
        return Mono.zip(
                branchRepositoryPort.countBySalonId(salonId, search),
                branchRepositoryPort.findBySalonId(salonId, page, size, search)
                        .flatMap(this::enrichWithSalon)
                        .collectList()
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }
}
