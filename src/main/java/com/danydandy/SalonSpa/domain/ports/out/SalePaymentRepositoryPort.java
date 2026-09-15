package com.danydandy.SalonSpa.domain.ports.out;

import com.danydandy.SalonSpa.domain.model.SalePayment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SalePaymentRepositoryPort {
    Mono<SalePayment> save(SalePayment payment);

    Flux<SalePayment> findBySaleId(Long saleId);
}
