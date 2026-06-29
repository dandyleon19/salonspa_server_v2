package com.danydandy.SalonSpa.domain.ports.in;

import com.danydandy.SalonSpa.domain.model.ServiceCategory;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PublicCatalogUseCase {
    Mono<List<ServiceCategory>> findCategoriesWithServices(Long salonId);
}
