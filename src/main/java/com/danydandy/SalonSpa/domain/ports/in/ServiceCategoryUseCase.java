package com.danydandy.SalonSpa.domain.ports.in;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.domain.model.ServiceCategory;
import reactor.core.publisher.Mono;

public interface ServiceCategoryUseCase {
    Mono<ServiceCategory> create(ServiceCategory serviceCategory);
    Mono<PageResponse<ServiceCategory>> findPage(int page, int size);
    Mono<ServiceCategory> findById(Long id);
    Mono<ServiceCategory> update(Long id, ServiceCategory serviceCategory);
    Mono<Void> delete(Long id);
}
