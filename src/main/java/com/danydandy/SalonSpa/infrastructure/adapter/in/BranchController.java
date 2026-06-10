package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.application.dto.request.CreateBranchRequest;
import com.danydandy.SalonSpa.application.dto.request.UpdateBranchRequest;
import com.danydandy.SalonSpa.application.dto.response.BranchResponse;
import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.application.mapper.RequestDtoMapper;
import com.danydandy.SalonSpa.domain.model.Branch;
import com.danydandy.SalonSpa.domain.ports.in.BranchUseCase;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.BranchMapper;
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
@RequestMapping("/api/branches")
@RequiredArgsConstructor
@Validated
public class BranchController {

    private final BranchUseCase branchUseCase;
    private final BranchMapper branchMapper;
    private final RequestDtoMapper requestDtoMapper;

    @PostMapping
    public Mono<ResponseEntity<BranchResponse>> create(@Valid @RequestBody CreateBranchRequest request) {
        return branchUseCase.create(requestDtoMapper.toBranch(request))
                .map(branchMapper::toResponse)
                .map(branch -> ResponseEntity.status(HttpStatus.CREATED).body(branch));
    }

    @GetMapping
    public Mono<ResponseEntity<PageResponse<Branch>>> getAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Positive @Max(100) int size
    ) {
        return branchUseCase.findPage(page, size)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<BranchResponse>> getById(@PathVariable @Positive Long id) {
        return branchUseCase.findById(id)
                .map(branchMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<BranchResponse>> update(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateBranchRequest request
    ) {
        return branchUseCase.update(id, requestDtoMapper.toBranch(request))
                .map(branchMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable @Positive Long id) {
        return branchUseCase.delete(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
