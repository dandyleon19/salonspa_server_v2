package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.application.dto.request.ReplaceUserServiceCommissionsRequest;
import com.danydandy.SalonSpa.application.dto.request.UpsertUserServiceCommissionRequest;
import com.danydandy.SalonSpa.application.dto.response.UserServiceCommissionResponse;
import com.danydandy.SalonSpa.domain.model.UserServiceCommission;
import com.danydandy.SalonSpa.domain.ports.in.UserServiceCommissionUseCase;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.UserServiceCommissionMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/service-commissions")
@RequiredArgsConstructor
@Validated
public class UserServiceCommissionController {

    private final UserServiceCommissionUseCase userServiceCommissionUseCase;
    private final UserServiceCommissionMapper userServiceCommissionMapper;

    @GetMapping
    public Mono<ResponseEntity<List<UserServiceCommissionResponse>>> getAll(
            @PathVariable @Positive Long userId
    ) {
        return userServiceCommissionUseCase.findByUserId(userId)
                .map(commissions -> commissions.stream().map(userServiceCommissionMapper::toResponse).toList())
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{serviceId}")
    public Mono<ResponseEntity<UserServiceCommissionResponse>> upsert(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long serviceId,
            @Valid @RequestBody UpsertUserServiceCommissionRequest request
    ) {
        return userServiceCommissionUseCase.upsert(userId, serviceId, request.getCommissionPercentage())
                .map(userServiceCommissionMapper::toResponse)
                .map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }

    @PutMapping
    public Mono<ResponseEntity<List<UserServiceCommissionResponse>>> replaceAll(
            @PathVariable @Positive Long userId,
            @Valid @RequestBody ReplaceUserServiceCommissionsRequest request
    ) {
        List<UserServiceCommission> commissions = request.getCommissions().stream()
                .map(item -> UserServiceCommission.builder()
                        .serviceId(item.getServiceId())
                        .commissionPercentage(item.getCommissionPercentage())
                        .build())
                .toList();

        return userServiceCommissionUseCase.replaceAll(userId, commissions)
                .map(result -> result.stream().map(userServiceCommissionMapper::toResponse).toList())
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{serviceId}")
    public Mono<ResponseEntity<Void>> delete(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long serviceId
    ) {
        return userServiceCommissionUseCase.delete(userId, serviceId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
