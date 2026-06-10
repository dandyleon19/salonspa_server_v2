package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.application.dto.request.CreateServiceRequest;
import com.danydandy.SalonSpa.application.dto.request.UpdateServiceRequest;
import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.application.dto.response.ServiceResponse;
import com.danydandy.SalonSpa.application.mapper.RequestDtoMapper;
import com.danydandy.SalonSpa.domain.ports.in.ServiceUseCase;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.ServiceMapper;
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
@RequestMapping("/api/services")
@RequiredArgsConstructor
@Validated
public class ServiceController {

    private final ServiceUseCase serviceUseCase;
    private final RequestDtoMapper requestDtoMapper;
    private final ServiceMapper serviceMapper;

    @PostMapping
    public Mono<ResponseEntity<ServiceResponse>> create(@Valid @RequestBody CreateServiceRequest request) {
        return serviceUseCase.create(requestDtoMapper.toService(request))
                .map(serviceMapper::toResponse)
                .map(service -> ResponseEntity.status(HttpStatus.CREATED).body(service));
    }

    @GetMapping
    public Mono<ResponseEntity<PageResponse<ServiceResponse>>> getAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Positive @Max(100) int size
    ) {
        return serviceUseCase.findPage(page, size)
                .map(pageResponse -> PageResponse.of(
                        pageResponse.content().stream().map(serviceMapper::toResponse).toList(),
                        pageResponse.page(),
                        pageResponse.size(),
                        pageResponse.totalElements()
                ))
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ServiceResponse>> getById(@PathVariable @Positive Long id) {
        return serviceUseCase.findById(id)
                .map(serviceMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ServiceResponse>> update(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateServiceRequest request
    ) {
        return serviceUseCase.update(id, requestDtoMapper.toService(request))
                .map(serviceMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable @Positive Long id) {
        return serviceUseCase.delete(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
