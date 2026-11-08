package com.danydandy.SalonSpa.domain.ports.out;

import com.danydandy.SalonSpa.domain.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepositoryPort {
    Mono<Product> save(Product product);
    Flux<Product> findAll(int page, int size, String search);
    Mono<Long> countAll(String search);
    Mono<Product> findById(Long id);
    Mono<Void> deleteById(Long id);
    Flux<Product> findBySalonId(Long salonId, int page, int size, String search);
    Mono<Long> countBySalonId(Long salonId, String search);
    Flux<Product> findByCategoryId(Long id);
    Mono<Integer> applyStockDelta(Long id, Integer delta);
}
