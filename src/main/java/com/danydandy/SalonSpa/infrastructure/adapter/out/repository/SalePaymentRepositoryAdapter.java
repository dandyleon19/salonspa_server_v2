package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.domain.model.SalePayment;
import com.danydandy.SalonSpa.domain.ports.out.SalePaymentRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.SalePaymentMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SalePaymentRepositoryAdapter implements SalePaymentRepositoryPort {

    private final SalePaymentRepository salePaymentRepository;
    private final SalePaymentMapper salePaymentMapper;

    @Override
    public Mono<SalePayment> save(SalePayment payment) {
        return salePaymentRepository.save(salePaymentMapper.toEntity(payment))
                .map(salePaymentMapper::toDomain);
    }

    @Override
    public Flux<SalePayment> findBySaleId(Long saleId) {
        return salePaymentRepository.findBySaleId(saleId)
                .map(salePaymentMapper::toDomain);
    }
}
