package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.application.dto.request.CreateServiceRequest;
import com.danydandy.SalonSpa.application.dto.request.UpdateServiceRequest;
import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.application.mapper.RequestDtoMapper;
import com.danydandy.SalonSpa.domain.model.Service;
import com.danydandy.SalonSpa.domain.ports.in.ServiceUseCase;
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

    @PostMapping
    public Mono<ResponseEntity<Service>> create(@Valid @RequestBody CreateServiceRequest request) {
        return serviceUseCase.create(requestDtoMapper.toService(request))
                .map(service -> ResponseEntity.status(HttpStatus.CREATED).body(service));
    }

    @GetMapping
    public Mono<ResponseEntity<PageResponse<Service>>> getAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Positive @Max(100) int size
    ) {
        return serviceUseCase.findPage(page, size)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Service>> getById(@PathVariable @Positive Long id) {
        return serviceUseCase.findById(id)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Service>> update(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateServiceRequest request
    ) {
        return serviceUseCase.update(id, requestDtoMapper.toService(request))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable @Positive Long id) {
        return serviceUseCase.delete(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
