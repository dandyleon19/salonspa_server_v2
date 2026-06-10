package com.danydandy.SalonSpa.domain.ports.in;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.domain.model.Salon;
import reactor.core.publisher.Mono;

public interface SalonUseCase {

    Mono<Salon> create(Salon salon);
    Mono<PageResponse<Salon>> findPage(int page, int size);
    Mono<Salon> findById(Long id);
    Mono<Salon> update(Long id, Salon salon);
    Mono<Void> delete(Long id);
}
