package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.application.dto.response.UserResponse;
import com.danydandy.SalonSpa.domain.model.AuthUser;
import com.danydandy.SalonSpa.domain.model.User;
import com.danydandy.SalonSpa.domain.ports.in.UserUseCase;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserUseCase userUseCase;
    private final UserMapper userMapper;

    @GetMapping
    public ResponseEntity<Flux<UserResponse>> getAll(Authentication auth) {
        AuthUser user = (AuthUser) auth.getPrincipal();
        if (user.getRole().equals("SUPER_ADMIN")) return new ResponseEntity<>(userUseCase.findAll().map(userMapper::toResponse), HttpStatus.OK);
        return new ResponseEntity<>(userUseCase.findBySalonId().map(userMapper::toResponse), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mono<UserResponse>> getById(@PathVariable Long id) {
        return new ResponseEntity<>(userUseCase.findById(id).map(userMapper::toResponse), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mono<UserResponse>> update(@PathVariable Long id, @RequestBody User user) {
        return new ResponseEntity<>(userUseCase.update(id, user).map(userMapper::toResponse), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Mono<Void>> delete(@PathVariable Long id) {
        return new ResponseEntity<>(userUseCase.delete(id), HttpStatus.NO_CONTENT);
    }
}
