package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.domain.model.Sale;
import com.danydandy.SalonSpa.domain.ports.out.SaleRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.SaleMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RequiredArgsConstructor
public class SaleRepositoryAdapter implements SaleRepositoryPort {

    private final SaleRepository saleRepository;
    private final SaleMapper saleMapper;

    @Override
    public Mono<Sale> save(Sale sale) {
        return saleRepository.save(saleMapper.toEntity(sale))
                .map(saleMapper::toDomain);
    }

    @Override
    public Mono<Sale> findById(Long id) {
        return saleRepository.findById(id)
                .map(saleMapper::toDomain);
    }

    @Override
    public Flux<Sale> findAll(int page, int size, Long branchId, Long clientId, String status, LocalDate from,
                              LocalDate to) {
        long offset = (long) page * size;
        return saleRepository.findPage(branchId, clientId, status, from, to, size, offset)
                .map(saleMapper::toDomain);
    }

    @Override
    public Mono<Long> countAll(Long branchId, Long clientId, String status, LocalDate from, LocalDate to) {
        return saleRepository.countFiltered(branchId, clientId, status, from, to);
    }

    @Override
    public Flux<Sale> findBySalonId(Long salonId, int page, int size, Long branchId, Long clientId, String status,
                                    LocalDate from, LocalDate to) {
        long offset = (long) page * size;
        return saleRepository.findPageBySalonId(salonId, branchId, clientId, status, from, to, size, offset)
                .map(saleMapper::toDomain);
    }

    @Override
    public Mono<Long> countBySalonId(Long salonId, Long branchId, Long clientId, String status, LocalDate from,
                                     LocalDate to) {
        return saleRepository.countBySalonId(salonId, branchId, clientId, status, from, to);
    }
}
