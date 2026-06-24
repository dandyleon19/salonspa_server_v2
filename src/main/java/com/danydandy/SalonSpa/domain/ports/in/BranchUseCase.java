package com.danydandy.SalonSpa.domain.ports.in;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.domain.model.Branch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchUseCase {
    Mono<Branch> create(Branch branch);
    Mono<PageResponse<Branch>> findPage(int page, int size, String search);
    Mono<Branch> findById(Long id);
    Mono<Branch> update(Long id, Branch branch);
    Mono<Void> delete(Long id);
    Flux<Branch> findBySalonId();
}
