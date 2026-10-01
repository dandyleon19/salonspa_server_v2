package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.application.security.SecurityHelper;
import com.danydandy.SalonSpa.domain.exception.BadRequestException;
import com.danydandy.SalonSpa.domain.exception.NotFoundException;
import com.danydandy.SalonSpa.domain.model.AuthUser;
import com.danydandy.SalonSpa.domain.model.Product;
import com.danydandy.SalonSpa.domain.model.ProductStockMovement;
import com.danydandy.SalonSpa.domain.model.StockMovementType;
import com.danydandy.SalonSpa.domain.ports.in.ProductStockMovementUseCase;
import com.danydandy.SalonSpa.domain.ports.out.ProductRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.ProductStockMovementRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ProductStockMovementServiceImpl implements ProductStockMovementUseCase {

    private final ProductStockMovementRepositoryPort movementRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    @Override
    public Mono<ProductStockMovement> create(Long productId, StockMovementType movementType, Integer quantityDelta, String reason) {
        if (quantityDelta == null || quantityDelta == 0) {
            return Mono.error(new BadRequestException("Quantity delta must not be zero"));
        }
        if (movementType == StockMovementType.RESTOCK && quantityDelta <= 0) {
            return Mono.error(new BadRequestException("Restock quantity must be positive"));
        }
        if (movementType != StockMovementType.RESTOCK && movementType != StockMovementType.ADJUSTMENT) {
            return Mono.error(new BadRequestException("Only RESTOCK or ADJUSTMENT can be recorded manually"));
        }

        return SecurityHelper.currentUser()
                .flatMap(authUser -> loadProduct(productId, authUser)
                        .flatMap(product -> productRepositoryPort.applyStockDelta(productId, quantityDelta)
                                .switchIfEmpty(Mono.error(new BadRequestException(
                                        "Resulting stock cannot be negative")))
                                .flatMap(resultingStock -> movementRepositoryPort.save(ProductStockMovement.builder()
                                        .productId(productId)
                                        .movementType(movementType)
                                        .quantityDelta(quantityDelta)
                                        .resultingStock(resultingStock)
                                        .reason(reason)
                                        .createdByUserId(authUser.getUserId())
                                        .build()))));
    }

    @Override
    public Mono<PageResponse<ProductStockMovement>> findPage(Long productId, int page, int size) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> loadProduct(productId, authUser)
                        .then(Mono.zip(
                                movementRepositoryPort.countByProductId(productId),
                                movementRepositoryPort.findPageByProductId(productId, page, size)
                                        .flatMap(this::enrichWithUserName)
                                        .collectList()
                        ))
                        .map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1())));
    }

    private Mono<Product> loadProduct(Long productId, AuthUser authUser) {
        return productRepositoryPort.findById(productId)
                .switchIfEmpty(Mono.error(NotFoundException.forResource("Product", productId)))
                .flatMap(product -> SecurityHelper.requireSameSalon(product, product.getSalonId(), authUser, "Product", productId));
    }

    private Mono<ProductStockMovement> enrichWithUserName(ProductStockMovement movement) {
        if (movement.getCreatedByUserId() == null) {
            return Mono.just(movement);
        }
        return userRepositoryPort.findById(movement.getCreatedByUserId())
                .map(user -> {
                    movement.setCreatedByUserName(user.getFirstName() + " " + user.getLastName());
                    return movement;
                })
                .defaultIfEmpty(movement);
    }
}
