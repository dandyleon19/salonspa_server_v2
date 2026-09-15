package com.danydandy.SalonSpa.domain.ports.out;

import com.danydandy.SalonSpa.domain.model.SaleItem;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SaleItemRepositoryPort {
    Mono<SaleItem> save(SaleItem item);

    Flux<SaleItem> findBySaleId(Long saleId);
}
