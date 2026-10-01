package com.danydandy.SalonSpa.domain.ports.in;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.domain.model.ProductCategory;
import reactor.core.publisher.Mono;

public interface ProductCategoryUseCase {
    Mono<ProductCategory> create(ProductCategory productCategory);
    Mono<PageResponse<ProductCategory>> findPage(int page, int size, String search);
    Mono<ProductCategory> findById(Long id);
    Mono<ProductCategory> update(Long id, ProductCategory productCategory);
    Mono<Void> delete(Long id);
}
