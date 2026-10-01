package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.application.security.SecurityHelper;
import com.danydandy.SalonSpa.application.util.SearchHelper;
import com.danydandy.SalonSpa.domain.exception.NotFoundException;
import com.danydandy.SalonSpa.domain.model.ProductCategory;
import com.danydandy.SalonSpa.domain.ports.in.ProductCategoryUseCase;
import com.danydandy.SalonSpa.domain.ports.out.ProductCategoryRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ProductCategoryServiceImpl implements ProductCategoryUseCase {

    private final ProductCategoryRepositoryPort productCategoryRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;

    @Override
    public Mono<ProductCategory> create(ProductCategory productCategory) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> {
                    productCategory.setSalonId(authUser.getSalonId());
                    return productCategoryRepositoryPort.save(productCategory);
                });
    }

    @Override
    public Mono<PageResponse<ProductCategory>> findPage(int page, int size, String search) {
        String searchFilter = SearchHelper.toLikePattern(search);
        return SecurityHelper.currentUser()
                .flatMap(authUser -> {
                    if (SecurityHelper.isSuperAdmin(authUser)) {
                        return paginateAll(page, size, searchFilter);
                    }
                    return paginateBySalonId(authUser.getSalonId(), page, size, searchFilter);
                });
    }

    @Override
    public Mono<ProductCategory> findById(Long id) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> productCategoryRepositoryPort.findById(id)
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("ProductCategory", id)))
                        .flatMap(category -> SecurityHelper.requireSameSalon(category, category.getSalonId(), authUser, "ProductCategory", id))
                        .flatMap(this::enrichWithProducts));
    }

    @Override
    public Mono<ProductCategory> update(Long id, ProductCategory productCategory) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> productCategoryRepositoryPort.findById(id)
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("ProductCategory", id)))
                        .flatMap(existing -> SecurityHelper.requireSameSalon(existing, existing.getSalonId(), authUser, "ProductCategory", id))
                        .flatMap(existing -> {
                            existing.setName(productCategory.getName());
                            existing.setDescription(productCategory.getDescription());
                            existing.setLongDescription(productCategory.getLongDescription());
                            return productCategoryRepositoryPort.save(existing);
                        }));
    }

    @Override
    public Mono<Void> delete(Long id) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> productCategoryRepositoryPort.findById(id)
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("ProductCategory", id)))
                        .flatMap(category -> SecurityHelper.requireSameSalon(category, category.getSalonId(), authUser, "ProductCategory", id))
                        .flatMap(category -> productCategoryRepositoryPort.deleteById(id)));
    }

    private Mono<ProductCategory> enrichWithProducts(ProductCategory productCategory) {
        return productRepositoryPort.findByCategoryId(productCategory.getId())
                .collectList()
                .map(products -> {
                    productCategory.setProducts(products);
                    return productCategory;
                });
    }

    private Mono<PageResponse<ProductCategory>> paginateAll(int page, int size, String search) {
        return Mono.zip(
                productCategoryRepositoryPort.countAll(search),
                productCategoryRepositoryPort.findAll(page, size, search)
                        .flatMap(this::enrichWithProducts)
                        .collectList()
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }

    private Mono<PageResponse<ProductCategory>> paginateBySalonId(Long salonId, int page, int size, String search) {
        return Mono.zip(
                productCategoryRepositoryPort.countBySalonId(salonId, search),
                productCategoryRepositoryPort.findBySalonId(salonId, page, size, search)
                        .flatMap(this::enrichWithProducts)
                        .collectList()
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }
}
