package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.domain.model.Product;
import com.danydandy.SalonSpa.domain.ports.out.ProductRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public Mono<Product> save(Product product) {
        return productRepository.save(productMapper.toEntity(product))
                .map(productMapper::toDomain);
    }

    @Override
    public Flux<Product> findAll(int page, int size) {
        return productRepository.findAllByOrderByCreatedAtAscIdAsc(PageRequest.of(page, size))
                .map(productMapper::toDomain);
    }

    @Override
    public Mono<Long> countAll() {
        return productRepository.count();
    }

    @Override
    public Mono<Product> findById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return productRepository.deleteById(id);
    }

    @Override
    public Flux<Product> findBySalonId(Long salonId, int page, int size) {
        return productRepository.findBySalonIdOrderByCreatedAtAscIdAsc(salonId, PageRequest.of(page, size))
                .map(productMapper::toDomain);
    }

    @Override
    public Mono<Long> countBySalonId(Long salonId) {
        return productRepository.countBySalonId(salonId);
    }

    @Override
    public Flux<Product> findByCategoryId(Long id) {
        return productRepository.findByCategoryIdOrderByCreatedAtAscIdAsc(id)
                .map(productMapper::toDomain);
    }

    @Override
    public Mono<Integer> applyStockDelta(Long id, Integer delta) {
        return productRepository.applyStockDelta(id, delta);
    }
}
