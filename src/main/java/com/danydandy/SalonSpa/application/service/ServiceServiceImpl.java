package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.domain.model.AuthUser;
import com.danydandy.SalonSpa.domain.model.Service;
import com.danydandy.SalonSpa.domain.ports.in.ServiceUseCase;
import com.danydandy.SalonSpa.domain.ports.out.ServiceRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceUseCase {

    private final ServiceRepositoryPort serviceRepositoryPort;

    @Override
    public Mono<Service> create(Service service) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (AuthUser) ctx.getAuthentication().getPrincipal())
                .flatMap(authUser -> {
                    service.setSalonId(authUser.getSalonId());
                    return serviceRepositoryPort.save(service);
                });
    }

    @Override
    public Mono<PageResponse<Service>> findPage(int page, int size) {
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
    public Mono<Service> findById(Long id) {
        return serviceRepositoryPort.findById(id);
    }

    @Override
    public Mono<Service> update(Long id, Service service) {
        return serviceRepositoryPort.findById(id)
                .flatMap(existing -> {
                    existing.setName(service.getName());
                    existing.setDescription(service.getDescription());
                    existing.setLongDescription(service.getLongDescription());
                    existing.setPrice(service.getPrice());
                    existing.setIsActive(service.getIsActive());
                    return serviceRepositoryPort.save(existing);
                });
    }

    @Override
    public Mono<Void> delete(Long id) {
        return serviceRepositoryPort.deleteById(id);
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
