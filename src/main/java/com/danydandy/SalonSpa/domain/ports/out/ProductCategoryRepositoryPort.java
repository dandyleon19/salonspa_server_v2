package com.danydandy.SalonSpa.domain.ports.out;

import com.danydandy.SalonSpa.domain.model.ProductCategory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductCategoryRepositoryPort {
    Mono<ProductCategory> save(ProductCategory productCategory);
    Flux<ProductCategory> findAll(int page, int size, String search);
    Mono<Long> countAll(String search);
    Mono<ProductCategory> findById(Long id);
    Mono<Void> deleteById(Long id);
    Flux<ProductCategory> findBySalonId(Long salonId, int page, int size, String search);
    Mono<Long> countBySalonId(Long salonId, String search);
}
