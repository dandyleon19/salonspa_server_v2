package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.domain.model.Salon;
import com.danydandy.SalonSpa.domain.ports.in.SalonUseCase;
import com.danydandy.SalonSpa.domain.ports.out.BranchRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.SalonRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SalonService implements SalonUseCase {

    private final SalonRepositoryPort salonRepositoryPort;
    private final BranchRepositoryPort branchRepositoryPort;

    @Override
    public Mono<Salon> create(Salon salon) {
        return salonRepositoryPort.save(salon);
    }

    @Override
    public Flux<Salon> findAll() {
        return salonRepositoryPort.findAll()
                .flatMap(salon -> branchRepositoryPort.findBySalonId(salon.getId())
                        .collectList()
                        .map(branches -> {
                            salon.setBranches(branches);
                            return salon;
                        }));
    }

    @Override
    public Mono<Salon> findById(Long id) {
        return salonRepositoryPort.findById(id)
                .flatMap(salon -> branchRepositoryPort.findBySalonId(salon.getId())
                        .collectList()
                        .map(branches -> {
                            salon.setBranches(branches);
                            return salon;
                        }));
    }

    @Override
    public Mono<Salon> update(Long id, Salon salon) {
        return salonRepositoryPort.findById(id)
                .flatMap(existing -> {
                    existing.setName(salon.getName());
                    existing.setPhone(salon.getPhone());
                    existing.setFiscalAddress(salon.getFiscalAddress());
                    existing.setSocialReason(salon.getSocialReason());
                    existing.setRucNumber(salon.getRucNumber());
                    return salonRepositoryPort.save(existing);
                });
    }

    @Override
    public Mono<Void> delete(Long id) {
        return salonRepositoryPort.deleteById(id);
    }
}
