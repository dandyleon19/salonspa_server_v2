package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.application.dto.request.CreateClientRequest;
import com.danydandy.SalonSpa.application.dto.request.UpdateClientRequest;
import com.danydandy.SalonSpa.application.dto.response.ClientResponse;
import com.danydandy.SalonSpa.application.dto.response.ClinicalRecordResponse;
import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.application.mapper.RequestDtoMapper;
import com.danydandy.SalonSpa.domain.ports.in.ClientUseCase;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.ClientMapper;
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
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Validated
public class ClientController {

    private final ClientUseCase clientUseCase;
    private final RequestDtoMapper requestDtoMapper;
    private final ClientMapper clientMapper;
    private final ClinicalRecordMapper clinicalRecordMapper;

    @PostMapping
    public Mono<ResponseEntity<ClientResponse>> create(@Valid @RequestBody CreateClientRequest request) {
        return clientUseCase.create(requestDtoMapper.toClient(request))
                .map(clientMapper::toResponse)
                .map(client -> ResponseEntity.status(HttpStatus.CREATED).body(client));
    }

    @GetMapping
    public Mono<ResponseEntity<PageResponse<ClientResponse>>> getAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Positive @Max(100) int size
    ) {
        return clientUseCase.findPage(page, size)
                .map(pageResponse -> PageResponse.of(
                        pageResponse.content().stream().map(clientMapper::toResponse).toList(),
                        pageResponse.page(),
                        pageResponse.size(),
                        pageResponse.totalElements()
                ))
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ClientResponse>> getById(@PathVariable @Positive Long id) {
        return clientUseCase.findById(id)
                .map(clientMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}/clinical-records")
    public Mono<ResponseEntity<PageResponse<ClinicalRecordResponse>>> getClinicalRecords(
            @PathVariable @Positive Long id,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Positive @Max(100) int size
    ) {
        return clientUseCase.findClinicalRecordsPage(id, page, size)
                .map(pageResponse -> PageResponse.of(
                        pageResponse.content().stream().map(clinicalRecordMapper::toResponse).toList(),
                        pageResponse.page(),
                        pageResponse.size(),
                        pageResponse.totalElements()
                ))
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ClientResponse>> update(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateClientRequest request
    ) {
        return clientUseCase.update(id, requestDtoMapper.toClient(request))
                .map(clientMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable @Positive Long id) {
        return clientUseCase.delete(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
