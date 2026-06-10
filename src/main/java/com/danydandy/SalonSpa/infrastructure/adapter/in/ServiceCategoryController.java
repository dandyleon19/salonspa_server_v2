package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.application.dto.request.CreateServiceCategoryRequest;
import com.danydandy.SalonSpa.application.dto.request.UpdateServiceCategoryRequest;
import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.application.dto.response.ServiceCategoryResponse;
import com.danydandy.SalonSpa.application.mapper.RequestDtoMapper;
import com.danydandy.SalonSpa.domain.ports.in.ServiceCategoryUseCase;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.ServiceCategoryMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/service-categories")
@RequiredArgsConstructor
@Validated
public class ServiceCategoryController {

    private final ServiceCategoryUseCase serviceCategoryUseCase;
    private final RequestDtoMapper requestDtoMapper;
    private final ServiceCategoryMapper serviceCategoryMapper;

    @PostMapping
    public Mono<ResponseEntity<ServiceCategoryResponse>> create(@Valid @RequestBody CreateServiceCategoryRequest request) {
        return serviceCategoryUseCase.create(requestDtoMapper.toServiceCategory(request))
                .map(serviceCategoryMapper::toResponse)
                .map(category -> ResponseEntity.status(HttpStatus.CREATED).body(category));
    }

    @GetMapping
    public Mono<ResponseEntity<PageResponse<ServiceCategoryResponse>>> getAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Positive @Max(100) int size
    ) {
        return serviceCategoryUseCase.findPage(page, size)
                .map(pageResponse -> PageResponse.of(
                        pageResponse.content().stream().map(serviceCategoryMapper::toResponse).toList(),
                        pageResponse.page(),
                        pageResponse.size(),
                        pageResponse.totalElements()
                ))
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ServiceCategoryResponse>> getById(@PathVariable @Positive Long id) {
        return serviceCategoryUseCase.findById(id)
                .map(serviceCategoryMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ServiceCategoryResponse>> update(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateServiceCategoryRequest request
    ) {
        return serviceCategoryUseCase.update(id, requestDtoMapper.toServiceCategory(request))
                .map(serviceCategoryMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable @Positive Long id) {
        return serviceCategoryUseCase.delete(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
