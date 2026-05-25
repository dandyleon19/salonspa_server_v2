package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.domain.model.AuthUser;
import com.danydandy.SalonSpa.domain.model.Service;
import com.danydandy.SalonSpa.domain.ports.in.ServiceUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceUseCase serviceUseCase;

    @PostMapping
    public ResponseEntity<Mono<Service>> create(@RequestBody Service service) {
        return new ResponseEntity<>(serviceUseCase.create(service), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Flux<Service>> getAll(Authentication auth) {
        AuthUser user = (AuthUser) auth.getPrincipal();
        if (user.getRole().equals("SUPER_ADMIN")) return new ResponseEntity<>(serviceUseCase.findAll(), HttpStatus.OK);
        return new ResponseEntity<>(serviceUseCase.findBySalonId(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mono<Service>> getById(@PathVariable Long id) {
        return new ResponseEntity<>(serviceUseCase.findById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mono<Service>> update(@PathVariable Long id, @RequestBody Service service) {
        return new ResponseEntity<>(serviceUseCase.update(id, service), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Mono<Void>> delete(@PathVariable Long id) {
        return new ResponseEntity<>(serviceUseCase.delete(id), HttpStatus.NO_CONTENT);
    }
}
