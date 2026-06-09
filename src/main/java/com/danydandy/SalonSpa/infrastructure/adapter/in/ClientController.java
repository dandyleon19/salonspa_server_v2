package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.application.dto.request.CreateClientRequest;
import com.danydandy.SalonSpa.application.dto.request.UpdateClientRequest;
import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.application.mapper.RequestDtoMapper;
import com.danydandy.SalonSpa.domain.model.Client;
import com.danydandy.SalonSpa.domain.model.ClinicalRecord;
import com.danydandy.SalonSpa.domain.ports.in.ClientUseCase;
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

    @PostMapping
    public Mono<ResponseEntity<Client>> create(@Valid @RequestBody CreateClientRequest request) {
        return clientUseCase.create(requestDtoMapper.toClient(request))
                .map(client -> ResponseEntity.status(HttpStatus.CREATED).body(client));
    }

    @GetMapping
    public Mono<ResponseEntity<PageResponse<Client>>> getAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Positive @Max(100) int size
    ) {
        return clientUseCase.findPage(page, size)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Client>> getById(@PathVariable @Positive Long id) {
        return clientUseCase.findById(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}/clinical-records")
    public Mono<ResponseEntity<PageResponse<ClinicalRecord>>> getClinicalRecords(
            @PathVariable @Positive Long id,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Positive @Max(100) int size
    ) {
        return clientUseCase.findClinicalRecordsPage(id, page, size)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Client>> update(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateClientRequest request
    ) {
        return clientUseCase.update(id, requestDtoMapper.toClient(request))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable @Positive Long id) {
        return clientUseCase.delete(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
