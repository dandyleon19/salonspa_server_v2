package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.domain.model.AuthUser;
import com.danydandy.SalonSpa.domain.model.Client;
import com.danydandy.SalonSpa.domain.ports.in.ClientUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientUseCase clientUseCase;

    @PostMapping
    public ResponseEntity<Mono<Client>> create(@RequestBody Client client) {
        return new ResponseEntity<>(clientUseCase.create(client), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Flux<Client>> getAll(Authentication auth) {
        AuthUser user = (AuthUser) auth.getPrincipal();
        return new ResponseEntity<>(clientUseCase.findBySalonId(user.getSalonId()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mono<Client>> getById(@PathVariable Long id) {
        return new ResponseEntity<>(clientUseCase.findById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mono<Client>> update(@PathVariable Long id, @RequestBody Client client) {
        return new ResponseEntity<>(clientUseCase.update(id, client), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Mono<Void>> delete(@PathVariable Long id) {
        return new ResponseEntity<>(clientUseCase.delete(id), HttpStatus.NO_CONTENT);
    }
}
