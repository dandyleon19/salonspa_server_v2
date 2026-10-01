package com.danydandy.SalonSpa.domain.ports.in;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.domain.model.Product;
import reactor.core.publisher.Mono;

public interface ProductUseCase {
    Mono<Product> create(Product product);
    Mono<PageResponse<Product>> findPage(int page, int size);
    Mono<Product> findById(Long id);
    Mono<Product> update(Long id, Product product);
    Mono<Product> updateImage(Long id, String imageUrl);
    Mono<Void> delete(Long id);
}
