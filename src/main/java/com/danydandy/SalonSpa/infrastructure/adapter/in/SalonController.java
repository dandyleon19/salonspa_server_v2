package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.application.dto.request.CreateSalonRequest;
import com.danydandy.SalonSpa.application.dto.request.UpdateSalonRequest;
import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.application.dto.response.SalonResponse;
import com.danydandy.SalonSpa.application.mapper.RequestDtoMapper;
import com.danydandy.SalonSpa.domain.ports.in.SalonUseCase;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.SalonMapper;
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
@RequestMapping("/api/salons")
@RequiredArgsConstructor
@Validated
public class SalonController {

    private final SalonUseCase salonUseCase;
    private final RequestDtoMapper requestDtoMapper;
    private final SalonMapper salonMapper;

    @PostMapping
    public Mono<ResponseEntity<SalonResponse>> create(@Valid @RequestBody CreateSalonRequest request) {
        return salonUseCase.create(requestDtoMapper.toSalon(request))
                .map(salonMapper::toResponse)
                .map(salon -> ResponseEntity.status(HttpStatus.CREATED).body(salon));
    }

    @GetMapping
    public Mono<ResponseEntity<PageResponse<SalonResponse>>> getAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Positive @Max(100) int size
    ) {
        return salonUseCase.findPage(page, size)
                .map(pageResponse -> PageResponse.of(
                        pageResponse.content().stream().map(salonMapper::toResponse).toList(),
                        pageResponse.page(),
                        pageResponse.size(),
                        pageResponse.totalElements()
                ))
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<SalonResponse>> getById(@PathVariable @Positive Long id) {
        return salonUseCase.findById(id)
                .map(salonMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<SalonResponse>> update(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateSalonRequest request
    ) {
        return salonUseCase.update(id, requestDtoMapper.toSalon(request))
                .map(salonMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable @Positive Long id) {
        return salonUseCase.delete(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
