package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.domain.model.SaleItem;
import com.danydandy.SalonSpa.domain.ports.out.SaleItemRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.SaleItemMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SaleItemRepositoryAdapter implements SaleItemRepositoryPort {

    private final SaleItemRepository saleItemRepository;
    private final SaleItemMapper saleItemMapper;

    @Override
    public Mono<SaleItem> save(SaleItem item) {
        return saleItemRepository.save(saleItemMapper.toEntity(item))
                .map(saleItemMapper::toDomain);
    }

    @Override
    public Flux<SaleItem> findBySaleId(Long saleId) {
        return saleItemRepository.findBySaleId(saleId)
                .map(saleItemMapper::toDomain);
    }
}
