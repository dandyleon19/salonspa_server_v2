package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.domain.model.ProductStockMovement;
import com.danydandy.SalonSpa.domain.ports.out.ProductStockMovementRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.ProductStockMovementMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ProductStockMovementRepositoryAdapter implements ProductStockMovementRepositoryPort {

    private final ProductStockMovementRepository repository;
    private final ProductStockMovementMapper mapper;

    @Override
    public Mono<ProductStockMovement> save(ProductStockMovement movement) {
        return repository.save(mapper.toEntity(movement))
                .map(mapper::toDomain);
    }

    @Override
    public Flux<ProductStockMovement> findPageByProductId(Long productId, int page, int size) {
        long offset = (long) page * size;
        return repository.findPageByProductId(productId, size, offset)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Long> countByProductId(Long productId) {
        return repository.countByProductId(productId);
    }
}
