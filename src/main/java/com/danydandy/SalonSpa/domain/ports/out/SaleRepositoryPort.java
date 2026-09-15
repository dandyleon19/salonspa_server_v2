package com.danydandy.SalonSpa.domain.ports.out;

import com.danydandy.SalonSpa.domain.model.Sale;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface SaleRepositoryPort {
    Mono<Sale> save(Sale sale);

    Mono<Sale> findById(Long id);

    Flux<Sale> findAll(int page, int size, Long branchId, Long clientId, String status, LocalDate from, LocalDate to);

    Mono<Long> countAll(Long branchId, Long clientId, String status, LocalDate from, LocalDate to);

    Flux<Sale> findBySalonId(Long salonId, int page, int size, Long branchId, Long clientId, String status,
                             LocalDate from, LocalDate to);

    Mono<Long> countBySalonId(Long salonId, Long branchId, Long clientId, String status, LocalDate from, LocalDate to);
}
