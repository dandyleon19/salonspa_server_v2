package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.domain.model.AuthUser;
import com.danydandy.SalonSpa.domain.model.ServiceCategory;
import com.danydandy.SalonSpa.domain.ports.in.ServiceCategoryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/service-categories")
@RequiredArgsConstructor
public class ServiceCategoryController {

    private final ServiceCategoryUseCase serviceCategoryUseCase;

    @PostMapping
    public ResponseEntity<Mono<ServiceCategory>> create(@RequestBody ServiceCategory serviceCategory) {
        return new ResponseEntity<>(serviceCategoryUseCase.create(serviceCategory), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Flux<ServiceCategory>> getAll(Authentication auth) {
        AuthUser user = (AuthUser) auth.getPrincipal();
        if (user.getRole().equals("SUPER_ADMIN")) return new ResponseEntity<>(serviceCategoryUseCase.findAll(), HttpStatus.OK);
        return new ResponseEntity<>(serviceCategoryUseCase.findBySalonId(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mono<ServiceCategory>> getById(@PathVariable Long id) {
        return new ResponseEntity<>(serviceCategoryUseCase.findById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mono<ServiceCategory>> update(@PathVariable Long id, @RequestBody ServiceCategory serviceCategory) {
        return new ResponseEntity<>(serviceCategoryUseCase.update(id, serviceCategory), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Mono<Void>> delete(@PathVariable Long id) {
        return new ResponseEntity<>(serviceCategoryUseCase.delete(id), HttpStatus.NO_CONTENT);
    }
}
