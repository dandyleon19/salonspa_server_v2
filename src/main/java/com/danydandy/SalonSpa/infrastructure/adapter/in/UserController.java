package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.domain.model.AuthUser;
import com.danydandy.SalonSpa.domain.model.User;
import com.danydandy.SalonSpa.domain.ports.in.UserUseCase;
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

    @GetMapping
    public ResponseEntity<Flux<User>> getAll(Authentication auth) {
        AuthUser user = (AuthUser) auth.getPrincipal();
        return new ResponseEntity<>(userUseCase.findBySalonId(user.getSalonId()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mono<User>> getById(@PathVariable Long id) {
        return new ResponseEntity<>(userUseCase.findById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mono<User>> update(@PathVariable Long id, @RequestBody User user) {
        return new ResponseEntity<>(userUseCase.update(id, user), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Mono<Void>> delete(@PathVariable Long id) {
        return new ResponseEntity<>(userUseCase.delete(id), HttpStatus.NO_CONTENT);
    }
}
