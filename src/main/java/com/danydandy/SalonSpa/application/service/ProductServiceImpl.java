package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.application.security.SecurityHelper;
import com.danydandy.SalonSpa.domain.exception.NotFoundException;
import com.danydandy.SalonSpa.domain.model.Product;
import com.danydandy.SalonSpa.domain.ports.in.ProductUseCase;
import com.danydandy.SalonSpa.domain.ports.out.ProductRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ProductServiceImpl implements ProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final FileStorageService fileStorageService;

    @Override
    public Mono<Product> create(Product product) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> {
                    product.setSalonId(authUser.getSalonId());
                    return productRepositoryPort.save(product);
                });
    }

    @Override
    public Mono<PageResponse<Product>> findPage(int page, int size) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> {
                    if (SecurityHelper.isSuperAdmin(authUser)) {
                        return paginateAll(page, size);
                    }
                    return paginateBySalonId(authUser.getSalonId(), page, size);
                });
    }

    @Override
    public Mono<Product> findById(Long id) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> productRepositoryPort.findById(id)
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("Product", id)))
                        .flatMap(product -> SecurityHelper.requireSameSalon(product, product.getSalonId(), authUser, "Product", id)));
    }

    @Override
    public Mono<Product> update(Long id, Product product) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> productRepositoryPort.findById(id)
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("Product", id)))
                        .flatMap(existing -> SecurityHelper.requireSameSalon(existing, existing.getSalonId(), authUser, "Product", id))
                        .flatMap(existing -> {
                            existing.setName(product.getName());
                            existing.setDescription(product.getDescription());
                            existing.setLongDescription(product.getLongDescription());
                            existing.setPrice(product.getPrice());
                            existing.setStockQuantity(product.getStockQuantity());
                            existing.setLowStockThreshold(product.getLowStockThreshold());
                            existing.setIsActive(product.getIsActive());
                            return productRepositoryPort.save(existing);
                        }));
    }

    @Override
    public Mono<Product> updateImage(Long id, String imageUrl) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> productRepositoryPort.findById(id)
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("Product", id)))
                        .flatMap(existing -> SecurityHelper.requireSameSalon(existing, existing.getSalonId(), authUser, "Product", id))
                        .flatMap(existing -> {
                            existing.setImageUrl(imageUrl);
                            return productRepositoryPort.save(existing);
                        }));
    }

    @Override
    public Mono<Void> delete(Long id) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> productRepositoryPort.findById(id)
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("Product", id)))
                        .flatMap(product -> SecurityHelper.requireSameSalon(product, product.getSalonId(), authUser, "Product", id))
                        .flatMap(product -> productRepositoryPort.deleteById(id)
                                .then(fileStorageService.deleteProductImage(id))));
    }

    private Mono<PageResponse<Product>> paginateAll(int page, int size) {
        return Mono.zip(
                productRepositoryPort.countAll(),
                productRepositoryPort.findAll(page, size).collectList()
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }

    private Mono<PageResponse<Product>> paginateBySalonId(Long salonId, int page, int size) {
        return Mono.zip(
                productRepositoryPort.countBySalonId(salonId),
                productRepositoryPort.findBySalonId(salonId, page, size).collectList()
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }
}
