package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.application.dto.request.CreateSalonRequest;
import com.danydandy.SalonSpa.application.dto.request.UpdateSalonRequest;
import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.application.mapper.RequestDtoMapper;
import com.danydandy.SalonSpa.domain.model.Salon;
import com.danydandy.SalonSpa.domain.ports.in.SalonUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/salons")
@RequiredArgsConstructor
@Validated
public class SalonController {

    private final SalonUseCase salonUseCase;
    private final RequestDtoMapper requestDtoMapper;

    @PostMapping
    public Mono<ResponseEntity<Salon>> create(@Valid @RequestBody CreateSalonRequest request) {
        return salonUseCase.create(requestDtoMapper.toSalon(request))
                .map(salon -> ResponseEntity.status(HttpStatus.CREATED).body(salon));
    }

    @GetMapping
    public Mono<ResponseEntity<PageResponse<Salon>>> getAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Positive @Max(100) int size
    ) {
        return salonUseCase.findPage(page, size)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Salon>> getById(@PathVariable @Positive Long id) {
        return salonUseCase.findById(id)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Salon>> update(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateSalonRequest request
    ) {
        return salonUseCase.update(id, requestDtoMapper.toSalon(request))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable @Positive Long id) {
        return salonUseCase.delete(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
