package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.application.security.SecurityHelper;
import com.danydandy.SalonSpa.application.util.SearchHelper;
import com.danydandy.SalonSpa.domain.exception.NotFoundException;
import com.danydandy.SalonSpa.domain.model.ServiceCategory;
import com.danydandy.SalonSpa.domain.ports.in.ServiceCategoryUseCase;
import com.danydandy.SalonSpa.domain.ports.out.ServiceCategoryRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.ServiceRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ServiceCategoryServiceImpl implements ServiceCategoryUseCase {

    private final ServiceCategoryRepositoryPort serviceCategoryRepositoryPort;
    private final ServiceRepositoryPort serviceRepositoryPort;

    @Override
    public Mono<ServiceCategory> create(ServiceCategory serviceCategory) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> {
                    serviceCategory.setSalonId(authUser.getSalonId());
                    return serviceCategoryRepositoryPort.save(serviceCategory);
                });
    }

    @Override
    public Mono<PageResponse<ServiceCategory>> findPage(int page, int size, String search) {
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
    public Mono<ServiceCategory> findById(Long id) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> serviceCategoryRepositoryPort.findById(id)
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("ServiceCategory", id)))
                        .flatMap(category -> SecurityHelper.requireSameSalon(category, category.getSalonId(), authUser, "ServiceCategory", id))
                        .flatMap(this::enrichWithServices));
    }

    @Override
    public Mono<ServiceCategory> update(Long id, ServiceCategory serviceCategory) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> serviceCategoryRepositoryPort.findById(id)
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("ServiceCategory", id)))
                        .flatMap(existing -> SecurityHelper.requireSameSalon(existing, existing.getSalonId(), authUser, "ServiceCategory", id))
                        .flatMap(existing -> {
                            existing.setName(serviceCategory.getName());
                            existing.setDescription(serviceCategory.getDescription());
                            existing.setLongDescription(serviceCategory.getLongDescription());
                            return serviceCategoryRepositoryPort.save(existing);
                        }));
    }

    @Override
    public Mono<Void> delete(Long id) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> serviceCategoryRepositoryPort.findById(id)
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("ServiceCategory", id)))
                        .flatMap(category -> SecurityHelper.requireSameSalon(category, category.getSalonId(), authUser, "ServiceCategory", id))
                        .flatMap(category -> serviceCategoryRepositoryPort.deleteById(id)));
    }

    private Mono<ServiceCategory> enrichWithServices(ServiceCategory serviceCategory) {
        return serviceRepositoryPort.findByCategoryId(serviceCategory.getId())
                .collectList()
                .map(services -> {
                    serviceCategory.setServices(services);
                    return serviceCategory;
                });
    }

    private Mono<PageResponse<ServiceCategory>> paginateAll(int page, int size, String search) {
        return Mono.zip(
                serviceCategoryRepositoryPort.countAll(search),
                serviceCategoryRepositoryPort.findAll(page, size, search)
                        .flatMap(this::enrichWithServices)
                        .collectList()
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }

    private Mono<PageResponse<ServiceCategory>> paginateBySalonId(Long salonId, int page, int size, String search) {
        return Mono.zip(
                serviceCategoryRepositoryPort.countBySalonId(salonId, search),
                serviceCategoryRepositoryPort.findBySalonId(salonId, page, size, search)
                        .flatMap(this::enrichWithServices)
                        .collectList()
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }
}
