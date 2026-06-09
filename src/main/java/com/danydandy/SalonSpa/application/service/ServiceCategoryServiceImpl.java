package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.domain.model.AuthUser;
import com.danydandy.SalonSpa.domain.model.ServiceCategory;
import com.danydandy.SalonSpa.domain.ports.in.ServiceCategoryUseCase;
import com.danydandy.SalonSpa.domain.ports.out.ServiceCategoryRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.ServiceRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ServiceCategoryServiceImpl implements ServiceCategoryUseCase {

    private final ServiceCategoryRepositoryPort serviceCategoryRepositoryPort;
    private final ServiceRepositoryPort serviceRepositoryPort;

    @Override
    public Mono<ServiceCategory> create(ServiceCategory serviceCategory) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (AuthUser) ctx.getAuthentication().getPrincipal())
                .flatMap(authUser -> {
                    serviceCategory.setSalonId(authUser.getSalonId());
                    return serviceCategoryRepositoryPort.save(serviceCategory);
                });
    }

    @Override
    public Mono<PageResponse<ServiceCategory>> findPage(int page, int size) {
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
    public Mono<ServiceCategory> findById(Long id) {
        return serviceCategoryRepositoryPort.findById(id)
                .flatMap(serviceCategory -> serviceRepositoryPort.findByCategoryId(serviceCategory.getId())
                        .collectList()
                        .map(services -> {
                            serviceCategory.setServices(services);
                            return serviceCategory;
                        }));
    }

    @Override
    public Mono<ServiceCategory> update(Long id, ServiceCategory serviceCategory) {
        return serviceCategoryRepositoryPort.findById(id)
                .flatMap(existing -> {
                    existing.setName(serviceCategory.getName());
                    existing.setDescription(serviceCategory.getDescription());
                    existing.setLongDescription(serviceCategory.getLongDescription());
                    return serviceCategoryRepositoryPort.save(existing);
                });
    }

    @Override
    public Mono<Void> delete(Long id) {
        return serviceCategoryRepositoryPort.deleteById(id);
    }

    private Mono<ServiceCategory> enrichWithServices(ServiceCategory serviceCategory) {
        return serviceRepositoryPort.findByCategoryId(serviceCategory.getId())
                .collectList()
                .map(services -> {
                    serviceCategory.setServices(services);
                    return serviceCategory;
                });
    }

    private Mono<PageResponse<ServiceCategory>> paginateAll(int page, int size) {
        return Mono.zip(
                serviceCategoryRepositoryPort.countAll(),
                serviceCategoryRepositoryPort.findAll(page, size)
                        .flatMap(this::enrichWithServices)
                        .collectList()
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }

    private Mono<PageResponse<ServiceCategory>> paginateBySalonId(Long salonId, int page, int size) {
        return Mono.zip(
                serviceCategoryRepositoryPort.countBySalonId(salonId),
                serviceCategoryRepositoryPort.findBySalonId(salonId, page, size)
                        .flatMap(this::enrichWithServices)
                        .collectList()
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }
}
