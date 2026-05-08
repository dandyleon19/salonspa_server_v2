package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.application.dto.response.BranchResponse;
import com.danydandy.SalonSpa.domain.model.AuthUser;
import com.danydandy.SalonSpa.domain.model.Branch;
import com.danydandy.SalonSpa.domain.ports.in.BranchUseCase;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.BranchMapper;
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
    private final BranchMapper branchMapper;

    @PostMapping
    public ResponseEntity<Mono<BranchResponse>> create(@RequestBody Branch branch) {
        return new ResponseEntity<>(branchUseCase.create(branch).map(branchMapper::toResponse), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Flux<BranchResponse>> getAll(Authentication auth) {
        AuthUser user = (AuthUser) auth.getPrincipal();
        if (user.getRole().equals("SUPER_ADMIN")) return new ResponseEntity<>(branchUseCase.findAll().map(branchMapper::toResponse), HttpStatus.OK);
        return new ResponseEntity<>(branchUseCase.findBySalonId().map(branchMapper::toResponse), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mono<BranchResponse>> getById(@PathVariable Long id) {
        return new ResponseEntity<>(branchUseCase.findById(id).map(branchMapper::toResponse), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mono<BranchResponse>> update(@PathVariable Long id, @RequestBody Branch branch) {
        return new ResponseEntity<>(branchUseCase.update(id, branch).map(branchMapper::toResponse), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Mono<Void>> delete(@PathVariable Long id) {
        return new ResponseEntity<>(branchUseCase.delete(id), HttpStatus.NO_CONTENT);
    }
}
