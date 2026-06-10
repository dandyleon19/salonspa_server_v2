package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.application.dto.request.CreateClinicalRecordRequest;
import com.danydandy.SalonSpa.application.dto.request.UpdateClinicalRecordRequest;
import com.danydandy.SalonSpa.application.dto.response.ClinicalRecordResponse;
import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.application.mapper.RequestDtoMapper;
import com.danydandy.SalonSpa.domain.ports.in.ClinicalRecordUseCase;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.ClinicalRecordMapper;
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
@RequestMapping("/api/clinical-records")
@RequiredArgsConstructor
@Validated
public class ClinicalRecordController {

    private final ClinicalRecordUseCase clinicalRecordUseCase;
    private final RequestDtoMapper requestDtoMapper;
    private final ClinicalRecordMapper clinicalRecordMapper;

    @PostMapping
    public Mono<ResponseEntity<ClinicalRecordResponse>> create(@Valid @RequestBody CreateClinicalRecordRequest request) {
        return clinicalRecordUseCase.create(requestDtoMapper.toClinicalRecord(request))
                .map(clinicalRecordMapper::toResponse)
                .map(record -> ResponseEntity.status(HttpStatus.CREATED).body(record));
    }

    @GetMapping
    public Mono<ResponseEntity<PageResponse<ClinicalRecordResponse>>> getAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Positive @Max(100) int size
    ) {
        return clinicalRecordUseCase.findPage(page, size)
                .map(pageResponse -> PageResponse.of(
                        pageResponse.content().stream().map(clinicalRecordMapper::toResponse).toList(),
                        pageResponse.page(),
                        pageResponse.size(),
                        pageResponse.totalElements()
                ))
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ClinicalRecordResponse>> getById(@PathVariable @Positive Long id) {
        return clinicalRecordUseCase.findById(id)
                .map(clinicalRecordMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ClinicalRecordResponse>> update(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateClinicalRecordRequest request
    ) {
        return clinicalRecordUseCase.update(id, requestDtoMapper.toClinicalRecord(request))
                .map(clinicalRecordMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable @Positive Long id) {
        return clinicalRecordUseCase.delete(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
