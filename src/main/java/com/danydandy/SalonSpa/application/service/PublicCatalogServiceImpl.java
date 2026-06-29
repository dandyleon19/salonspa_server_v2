package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.domain.exception.NotFoundException;
import com.danydandy.SalonSpa.domain.model.ServiceCategory;
import com.danydandy.SalonSpa.domain.ports.in.PublicCatalogUseCase;
import com.danydandy.SalonSpa.domain.ports.out.SalonRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.ServiceCategoryRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.ServiceRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class PublicCatalogServiceImpl implements PublicCatalogUseCase {

    private static final int MAX_CATEGORIES = 100;

    private final SalonRepositoryPort salonRepositoryPort;
    private final ServiceCategoryRepositoryPort serviceCategoryRepositoryPort;
    private final ServiceRepositoryPort serviceRepositoryPort;

    @Override
    public Mono<List<ServiceCategory>> findCategoriesWithServices(Long salonId) {
        return salonRepositoryPort.findById(salonId)
                .switchIfEmpty(Mono.error(NotFoundException.forResource("Salon", salonId)))
                .thenMany(serviceCategoryRepositoryPort.findBySalonId(salonId, 0, MAX_CATEGORIES, null))
                .flatMap(this::enrichWithActiveServices)
                .collectList();
    }

    private Mono<ServiceCategory> enrichWithActiveServices(ServiceCategory category) {
        return serviceRepositoryPort.findByCategoryId(category.getId())
                .filter(service -> Boolean.TRUE.equals(service.getIsActive()))
                .collectList()
                .map(services -> {
                    category.setServices(services);
                    return category;
                });
    }
}
