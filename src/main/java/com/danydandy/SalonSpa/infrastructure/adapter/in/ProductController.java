package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.application.dto.request.CreateProductRequest;
import com.danydandy.SalonSpa.application.dto.request.CreateProductStockMovementRequest;
import com.danydandy.SalonSpa.application.dto.request.UpdateProductRequest;
import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.application.dto.response.ProductResponse;
import com.danydandy.SalonSpa.application.dto.response.ProductStockMovementResponse;
import com.danydandy.SalonSpa.application.mapper.RequestDtoMapper;
import com.danydandy.SalonSpa.domain.ports.in.ProductStockMovementUseCase;
import com.danydandy.SalonSpa.domain.ports.in.ProductUseCase;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.ProductMapper;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.ProductStockMovementMapper;
import com.danydandy.SalonSpa.infrastructure.storage.FileStorageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductUseCase productUseCase;
    private final RequestDtoMapper requestDtoMapper;
    private final ProductMapper productMapper;
    private final FileStorageService fileStorageService;
    private final ProductStockMovementUseCase productStockMovementUseCase;
    private final ProductStockMovementMapper productStockMovementMapper;

    @PostMapping
    public Mono<ResponseEntity<ProductResponse>> create(@Valid @RequestBody CreateProductRequest request) {
        return productUseCase.create(requestDtoMapper.toProduct(request))
                .map(productMapper::toResponse)
                .map(product -> ResponseEntity.status(HttpStatus.CREATED).body(product));
    }

    @GetMapping
    public Mono<ResponseEntity<PageResponse<ProductResponse>>> getAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Positive @Max(100) int size
    ) {
        return productUseCase.findPage(page, size)
                .map(pageResponse -> PageResponse.of(
                        pageResponse.content().stream().map(productMapper::toResponse).toList(),
                        pageResponse.page(),
                        pageResponse.size(),
                        pageResponse.totalElements()
                ))
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProductResponse>> getById(@PathVariable @Positive Long id) {
        return productUseCase.findById(id)
                .map(productMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ProductResponse>> update(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        return productUseCase.update(id, requestDtoMapper.toProduct(request))
                .map(productMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable @Positive Long id) {
        return productUseCase.delete(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @PostMapping("/{id}/image")
    public Mono<ResponseEntity<ProductResponse>> uploadImage(
            @PathVariable @Positive Long id,
            @RequestPart("file") Mono<FilePart> filePartMono
    ) {
        return filePartMono
                .flatMap(filePart -> fileStorageService.storeProductImage(id, filePart))
                .flatMap(imageUrl -> productUseCase.updateImage(id, imageUrl))
                .map(productMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}/image")
    public Mono<ResponseEntity<ProductResponse>> deleteImage(@PathVariable @Positive Long id) {
        return productUseCase.updateImage(id, null)
                .flatMap(product -> fileStorageService.deleteProductImage(id).thenReturn(product))
                .map(productMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/{id}/stock-movements")
    public Mono<ResponseEntity<ProductStockMovementResponse>> createStockMovement(
            @PathVariable @Positive Long id,
            @Valid @RequestBody CreateProductStockMovementRequest request
    ) {
        return productStockMovementUseCase.create(id, request.getMovementType(), request.getQuantityDelta(), request.getReason())
                .map(productStockMovementMapper::toResponse)
                .map(movement -> ResponseEntity.status(HttpStatus.CREATED).body(movement));
    }

    @GetMapping("/{id}/stock-movements")
    public Mono<ResponseEntity<PageResponse<ProductStockMovementResponse>>> getStockMovements(
            @PathVariable @Positive Long id,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Positive @Max(100) int size
    ) {
        return productStockMovementUseCase.findPage(id, page, size)
                .map(pageResponse -> PageResponse.of(
                        pageResponse.content().stream().map(productStockMovementMapper::toResponse).toList(),
                        pageResponse.page(),
                        pageResponse.size(),
                        pageResponse.totalElements()
                ))
                .map(ResponseEntity::ok);
    }
}
