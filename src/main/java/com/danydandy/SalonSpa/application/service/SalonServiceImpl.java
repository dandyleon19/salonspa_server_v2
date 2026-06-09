package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.application.security.SecurityHelper;
import com.danydandy.SalonSpa.domain.exception.NotFoundException;
import com.danydandy.SalonSpa.domain.model.AuthUser;
import com.danydandy.SalonSpa.domain.model.Salon;
import com.danydandy.SalonSpa.domain.ports.in.SalonUseCase;
import com.danydandy.SalonSpa.domain.ports.out.BranchRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.SalonRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class SalonServiceImpl implements SalonUseCase {

    private final SalonRepositoryPort salonRepositoryPort;
    private final BranchRepositoryPort branchRepositoryPort;

    @Override
    public Mono<Salon> create(Salon salon) {
        return salonRepositoryPort.save(salon);
    }

    @Override
    public Mono<PageResponse<Salon>> findPage(int page, int size) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> {
                    if (SecurityHelper.isSuperAdmin(authUser)) {
                        return paginateAll(page, size);
                    }
                    if (page > 0) {
                        return Mono.just(PageResponse.of(List.of(), page, size, 1L));
                    }
                    return salonRepositoryPort.findById(authUser.getSalonId())
                            .switchIfEmpty(Mono.error(NotFoundException.forResource("Salon", authUser.getSalonId())))
                            .flatMap(this::enrichWithBranches)
                            .map(salon -> PageResponse.of(List.of(salon), 0, size, 1L));
                });
    }

    @Override
    public Mono<Salon> findById(Long id) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> salonRepositoryPort.findById(id)
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("Salon", id)))
                        .flatMap(salon -> SecurityHelper.requireSalonAccess(id, authUser, "Salon", id)
                                .thenReturn(salon))
                        .flatMap(this::enrichWithBranches));
    }

    @Override
    public Mono<Salon> update(Long id, Salon salonUpdate) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> salonRepositoryPort.findById(id)
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("Salon", id)))
                        .flatMap(existing -> SecurityHelper.requireSalonAccess(id, authUser, "Salon", id)
                                .thenReturn(existing))
                        .flatMap(existing -> {
                            existing.setName(salonUpdate.getName());
                            existing.setPhone(salonUpdate.getPhone());
                            existing.setFiscalAddress(salonUpdate.getFiscalAddress());
                            existing.setSocialReason(salonUpdate.getSocialReason());
                            existing.setRucNumber(salonUpdate.getRucNumber());
                            return salonRepositoryPort.save(existing);
                        }));
    }

    @Override
    public Mono<Void> delete(Long id) {
        return salonRepositoryPort.findById(id)
                .switchIfEmpty(Mono.error(NotFoundException.forResource("Salon", id)))
                .flatMap(salon -> salonRepositoryPort.deleteById(id));
    }

    private Mono<Salon> enrichWithBranches(Salon salon) {
        return branchRepositoryPort.findBySalonId(salon.getId(), 0, 100)
                .collectList()
                .map(branches -> {
                    salon.setBranches(branches);
                    return salon;
                });
    }

    private Mono<PageResponse<Salon>> paginateAll(int page, int size) {
        return Mono.zip(
                salonRepositoryPort.countAll(),
                salonRepositoryPort.findAll(page, size)
                        .flatMap(this::enrichWithBranches)
                        .collectList()
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }
}
