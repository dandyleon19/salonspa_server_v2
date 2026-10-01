package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.application.dto.request.CreateProductCategoryRequest;
import com.danydandy.SalonSpa.application.dto.request.UpdateProductCategoryRequest;
import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.application.dto.response.ProductCategoryResponse;
import com.danydandy.SalonSpa.application.mapper.RequestDtoMapper;
import com.danydandy.SalonSpa.domain.ports.in.ProductCategoryUseCase;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.ProductCategoryMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/product-categories")
@RequiredArgsConstructor
@Validated
public class ProductCategoryController {

    private final ProductCategoryUseCase productCategoryUseCase;
    private final RequestDtoMapper requestDtoMapper;
    private final ProductCategoryMapper productCategoryMapper;

    @PostMapping
    public Mono<ResponseEntity<ProductCategoryResponse>> create(@Valid @RequestBody CreateProductCategoryRequest request) {
        return productCategoryUseCase.create(requestDtoMapper.toProductCategory(request))
                .map(productCategoryMapper::toResponse)
                .map(category -> ResponseEntity.status(HttpStatus.CREATED).body(category));
    }

    @GetMapping
    public Mono<ResponseEntity<PageResponse<ProductCategoryResponse>>> getAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Positive @Max(100) int size,
            @RequestParam(required = false) @Size(max = 255) String search
    ) {
        return productCategoryUseCase.findPage(page, size, search)
                .map(pageResponse -> PageResponse.of(
                        pageResponse.content().stream().map(productCategoryMapper::toResponse).toList(),
                        pageResponse.page(),
                        pageResponse.size(),
                        pageResponse.totalElements()
                ))
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProductCategoryResponse>> getById(@PathVariable @Positive Long id) {
        return productCategoryUseCase.findById(id)
                .map(productCategoryMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ProductCategoryResponse>> update(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateProductCategoryRequest request
    ) {
        return productCategoryUseCase.update(id, requestDtoMapper.toProductCategory(request))
                .map(productCategoryMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable @Positive Long id) {
        return productCategoryUseCase.delete(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
