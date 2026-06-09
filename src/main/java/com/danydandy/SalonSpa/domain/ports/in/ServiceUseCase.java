package com.danydandy.SalonSpa.domain.ports.in;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.domain.model.Service;
import reactor.core.publisher.Mono;

public interface ServiceUseCase {
    Mono<Service> create(Service service);
    Mono<PageResponse<Service>> findPage(int page, int size);
    Mono<Service> findById(Long id);
    Mono<Service> update(Long id, Service service);
    Mono<Void> delete(Long id);
}
