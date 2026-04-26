package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.domain.model.AuthUser;
import com.danydandy.SalonSpa.domain.model.Branch;
import com.danydandy.SalonSpa.domain.ports.in.BranchUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchUseCase branchUseCase;

    @PostMapping
    public ResponseEntity<Mono<Branch>> create(@RequestBody Branch branch) {
        return new ResponseEntity<>(branchUseCase.create(branch), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Flux<Branch>> getAll(Authentication auth) {
        AuthUser user = (AuthUser) auth.getPrincipal();
        return new ResponseEntity<>(branchUseCase.findBySalonId(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mono<Branch>> getById(@PathVariable Long id) {
        return new ResponseEntity<>(branchUseCase.findById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mono<Branch>> update(@PathVariable Long id, @RequestBody Branch branch) {
        return new ResponseEntity<>(branchUseCase.update(id, branch), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Mono<Void>> delete(@PathVariable Long id) {
        return new ResponseEntity<>(branchUseCase.delete(id), HttpStatus.NO_CONTENT);
    }
}
