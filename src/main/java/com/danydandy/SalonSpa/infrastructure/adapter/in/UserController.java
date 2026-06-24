package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.application.dto.request.UpdateUserRequest;
import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.application.dto.response.UserResponse;
import com.danydandy.SalonSpa.application.mapper.RequestDtoMapper;
import com.danydandy.SalonSpa.domain.model.Role;
import com.danydandy.SalonSpa.domain.ports.in.UserUseCase;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.UserMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserUseCase userUseCase;
    private final UserMapper userMapper;
    private final RequestDtoMapper requestDtoMapper;

    @GetMapping
    public Mono<ResponseEntity<PageResponse<UserResponse>>> getAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Positive @Max(100) int size,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) @Size(max = 255) String search
    ) {
        return userUseCase.findPage(page, size, isActive, role, search)
                .map(pageResponse -> PageResponse.of(
                        pageResponse.content().stream().map(userMapper::toResponse).toList(),
                        pageResponse.page(),
                        pageResponse.size(),
                        pageResponse.totalElements()
                ))
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> getById(@PathVariable @Positive Long id) {
        return userUseCase.findById(id)
                .map(userMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> update(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        return userUseCase.update(id, requestDtoMapper.toUser(request))
                .map(userMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable @Positive Long id) {
        return userUseCase.delete(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
