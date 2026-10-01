package com.danydandy.SalonSpa.domain.ports.out;

import com.danydandy.SalonSpa.domain.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepositoryPort {
    Mono<Product> save(Product product);
    Flux<Product> findAll(int page, int size);
    Mono<Long> countAll();
    Mono<Product> findById(Long id);
    Mono<Void> deleteById(Long id);
    Flux<Product> findBySalonId(Long salonId, int page, int size);
    Mono<Long> countBySalonId(Long salonId);
    Flux<Product> findByCategoryId(Long id);
    Mono<Integer> applyStockDelta(Long id, Integer delta);
}
