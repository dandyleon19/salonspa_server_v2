package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.domain.model.Salon;
import com.danydandy.SalonSpa.domain.ports.in.SalonUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/salons")
@RequiredArgsConstructor
public class SalonController {

    private final SalonUseCase salonUseCase;

    @PostMapping
    public ResponseEntity<Mono<Salon>> create(@RequestBody Salon salon) {
        return new ResponseEntity<>(salonUseCase.create(salon), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Flux<Salon>> getAll() {
        return new ResponseEntity<>(salonUseCase.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mono<Salon>> getById(@PathVariable Long id) {
        return new ResponseEntity<>(salonUseCase.findById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mono<Salon>> update(@PathVariable Long id, @RequestBody Salon salon) {
        return new ResponseEntity<>(salonUseCase.update(id, salon), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Mono<Void>> delete(@PathVariable Long id) {
        return new ResponseEntity<>(salonUseCase.delete(id), HttpStatus.NO_CONTENT);
    }
}
