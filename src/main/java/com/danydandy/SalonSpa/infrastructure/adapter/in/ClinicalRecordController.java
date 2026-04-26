package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.domain.model.ClinicalRecord;
import com.danydandy.SalonSpa.domain.ports.in.ClinicalRecordUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/clinical-records")
@RequiredArgsConstructor
public class ClinicalRecordController {

    private final ClinicalRecordUseCase clinicalRecordUseCase;

    @PostMapping
    public ResponseEntity<Mono<ClinicalRecord>> create(@RequestBody ClinicalRecord clinicalRecord) {
        return new ResponseEntity<>(clinicalRecordUseCase.create(clinicalRecord), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Flux<ClinicalRecord>> getAll() {
        return new ResponseEntity<>(clinicalRecordUseCase.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mono<ClinicalRecord>> getById(@PathVariable Long id) {
        return new ResponseEntity<>(clinicalRecordUseCase.findById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mono<ClinicalRecord>> update(@PathVariable Long id, @RequestBody ClinicalRecord clinicalRecord) {
        return new ResponseEntity<>(clinicalRecordUseCase.update(id, clinicalRecord), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Mono<Void>> delete(@PathVariable Long id) {
        return new ResponseEntity<>(clinicalRecordUseCase.delete(id), HttpStatus.NO_CONTENT);
    }
}
