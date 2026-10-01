package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.domain.model.ProductCategory;
import com.danydandy.SalonSpa.domain.ports.out.ProductCategoryRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.ProductCategoryMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ProductCategoryRepositoryAdapter implements ProductCategoryRepositoryPort {

    private final ProductCategoryRepository productCategoryRepository;
    private final ProductCategoryMapper productCategoryMapper;

    @Override
    public Mono<ProductCategory> save(ProductCategory productCategory) {
        return productCategoryRepository.save(productCategoryMapper.toEntity(productCategory))
                .map(productCategoryMapper::toDomain);
    }

    @Override
    public Flux<ProductCategory> findAll(int page, int size, String search) {
        long offset = (long) page * size;
        return productCategoryRepository.findPage(search, size, offset)
                .map(productCategoryMapper::toDomain);
    }

    @Override
    public Mono<Long> countAll(String search) {
        return productCategoryRepository.countFiltered(search);
    }

    @Override
    public Mono<ProductCategory> findById(Long id) {
        return productCategoryRepository.findById(id)
                .map(productCategoryMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return productCategoryRepository.deleteById(id);
    }

    @Override
    public Flux<ProductCategory> findBySalonId(Long salonId, int page, int size, String search) {
        long offset = (long) page * size;
        return productCategoryRepository.findPageBySalonId(salonId, search, size, offset)
                .map(productCategoryMapper::toDomain);
    }

    @Override
    public Mono<Long> countBySalonId(Long salonId, String search) {
        return productCategoryRepository.countBySalonId(salonId, search);
    }
}
