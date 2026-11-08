package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.application.security.SecurityHelper;
import com.danydandy.SalonSpa.application.util.SearchHelper;
import com.danydandy.SalonSpa.domain.exception.NotFoundException;
import com.danydandy.SalonSpa.domain.model.Salon;
import com.danydandy.SalonSpa.domain.model.SalonLastSession;
import com.danydandy.SalonSpa.domain.ports.in.SalonUseCase;
import com.danydandy.SalonSpa.domain.ports.in.SubscriptionUseCase;
import com.danydandy.SalonSpa.domain.ports.out.BranchRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.SalonRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class SalonServiceImpl implements SalonUseCase {

    private final SalonRepositoryPort salonRepositoryPort;
    private final BranchRepositoryPort branchRepositoryPort;
    private final SubscriptionUseCase subscriptionUseCase;

    @Override
    public Mono<Salon> create(Salon salon) {
        // A new salon starts its free trial right away.
        return salonRepositoryPort.save(salon)
                .flatMap(saved -> subscriptionUseCase.startTrial(saved.getId()).thenReturn(saved));
    }

    @Override
    public Mono<PageResponse<Salon>> findPage(int page, int size, String search) {
        String searchFilter = SearchHelper.toLikePattern(search);
        return SecurityHelper.currentUser()
                .flatMap(authUser -> {
                    if (SecurityHelper.isSuperAdmin(authUser)) {
                        return paginateAll(page, size, searchFilter);
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
    public Mono<Salon> updateLogo(Long id, String logoUrl) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> salonRepositoryPort.findById(id)
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("Salon", id)))
                        .flatMap(existing -> SecurityHelper.requireSalonAccess(id, authUser, "Salon", id)
                                .thenReturn(existing))
                        .flatMap(existing -> {
                            existing.setLogoUrl(logoUrl);
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
        return branchRepositoryPort.findBySalonId(salon.getId(), 0, 100, null)
                .collectList()
                .map(branches -> {
                    salon.setBranches(branches);
                    return salon;
                });
    }

    private Mono<PageResponse<Salon>> paginateAll(int page, int size, String search) {
        return Mono.zip(
                salonRepositoryPort.countAll(search),
                salonRepositoryPort.findAll(page, size, search)
                        .flatMap(this::enrichWithBranches)
                        .collectList()
                        .flatMap(this::withLastSessions)
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }

    /** One lookup for the whole page: when each salon last had a session, and whose it was. */
    private Mono<List<Salon>> withLastSessions(List<Salon> salons) {
        if (salons.isEmpty()) {
            return Mono.just(salons);
        }
        List<Long> ids = salons.stream().map(Salon::getId).toList();
        return salonRepositoryPort.findLastSessions(ids)
                .collectMap(SalonLastSession::salonId)
                .map(bySalon -> {
                    salons.forEach(salon -> {
                        SalonLastSession last = bySalon.get(salon.getId());
                        if (last != null) {
                            salon.setLastSessionAt(last.at());
                            salon.setLastSessionBy(last.userName());
                        }
                    });
                    return salons;
                });
    }
}
