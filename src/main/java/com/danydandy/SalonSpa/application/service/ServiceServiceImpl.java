package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.application.security.SecurityHelper;
import com.danydandy.SalonSpa.domain.exception.NotFoundException;
import com.danydandy.SalonSpa.domain.model.Service;
import com.danydandy.SalonSpa.domain.ports.in.ServiceUseCase;
import com.danydandy.SalonSpa.domain.ports.out.ServiceRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceUseCase {

    private final ServiceRepositoryPort serviceRepositoryPort;

    @Override
    public Mono<Service> create(Service service) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> {
                    service.setSalonId(authUser.getSalonId());
                    return serviceRepositoryPort.save(service);
                });
    }

    @Override
    public Mono<PageResponse<Service>> findPage(int page, int size) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> {
                    if (SecurityHelper.isSuperAdmin(authUser)) {
                        return paginateAll(page, size);
                    }
                    return paginateBySalonId(authUser.getSalonId(), page, size);
                });
    }

    @Override
    public Mono<Service> findById(Long id) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> serviceRepositoryPort.findById(id)
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("Service", id)))
                        .flatMap(service -> SecurityHelper.requireSameSalon(service, service.getSalonId(), authUser, "Service", id)));
    }

    @Override
    public Mono<Service> update(Long id, Service service) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> serviceRepositoryPort.findById(id)
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("Service", id)))
                        .flatMap(existing -> SecurityHelper.requireSameSalon(existing, existing.getSalonId(), authUser, "Service", id))
                        .flatMap(existing -> {
                            existing.setName(service.getName());
                            existing.setDescription(service.getDescription());
                            existing.setLongDescription(service.getLongDescription());
                            existing.setPrice(service.getPrice());
                            existing.setIsActive(service.getIsActive());
                            return serviceRepositoryPort.save(existing);
                        }));
    }

    @Override
    public Mono<Void> delete(Long id) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> serviceRepositoryPort.findById(id)
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("Service", id)))
                        .flatMap(service -> SecurityHelper.requireSameSalon(service, service.getSalonId(), authUser, "Service", id))
                        .flatMap(service -> serviceRepositoryPort.deleteById(id)));
    }

    private Mono<PageResponse<Service>> paginateAll(int page, int size) {
        return Mono.zip(
                serviceRepositoryPort.countAll(),
                serviceRepositoryPort.findAll(page, size).collectList()
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }

    private Mono<PageResponse<Service>> paginateBySalonId(Long salonId, int page, int size) {
        return Mono.zip(
                serviceRepositoryPort.countBySalonId(salonId),
                serviceRepositoryPort.findBySalonId(salonId, page, size).collectList()
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }
}
