package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.application.dto.request.CreateServiceCategoryRequest;
import com.danydandy.SalonSpa.application.dto.request.UpdateServiceCategoryRequest;
import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.application.mapper.RequestDtoMapper;
import com.danydandy.SalonSpa.domain.model.ServiceCategory;
import com.danydandy.SalonSpa.domain.ports.in.ServiceCategoryUseCase;
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

    @PostMapping
    public Mono<ResponseEntity<ServiceCategory>> create(@Valid @RequestBody CreateServiceCategoryRequest request) {
        return serviceCategoryUseCase.create(requestDtoMapper.toServiceCategory(request))
                .map(category -> ResponseEntity.status(HttpStatus.CREATED).body(category));
    }

    @GetMapping
    public Mono<ResponseEntity<PageResponse<ServiceCategory>>> getAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Positive @Max(100) int size
    ) {
        return serviceCategoryUseCase.findPage(page, size)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ServiceCategory>> getById(@PathVariable @Positive Long id) {
        return serviceCategoryUseCase.findById(id)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ServiceCategory>> update(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateServiceCategoryRequest request
    ) {
        return serviceCategoryUseCase.update(id, requestDtoMapper.toServiceCategory(request))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable @Positive Long id) {
        return serviceCategoryUseCase.delete(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
