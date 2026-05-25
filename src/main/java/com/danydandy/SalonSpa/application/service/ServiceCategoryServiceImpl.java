package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.domain.model.AuthUser;
import com.danydandy.SalonSpa.domain.model.ServiceCategory;
import com.danydandy.SalonSpa.domain.ports.in.ServiceCategoryUseCase;
import com.danydandy.SalonSpa.domain.ports.out.ServiceCategoryRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.ServiceRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Flux;
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
    public Flux<ServiceCategory> findAll() {
        return serviceCategoryRepositoryPort.findAll()
                .flatMap(serviceCategory -> serviceRepositoryPort.findByCategoryId(serviceCategory.getId())
                        .collectList()
                        .map(services -> {
                            serviceCategory.setServices(services);
                            return serviceCategory;
                        }));
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

    @Override
    public Flux<ServiceCategory> findBySalonId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (AuthUser) ctx.getAuthentication().getPrincipal())
                .flatMapMany(authUser ->
                        serviceCategoryRepositoryPort.findBySalonId(authUser.getSalonId())
                                .flatMap(serviceCategory -> serviceRepositoryPort.findByCategoryId(serviceCategory.getId())
                                        .collectList()
                                        .map(services -> {
                                            serviceCategory.setServices(services);
                                            return serviceCategory;
                                        }))
                );
    }
}
