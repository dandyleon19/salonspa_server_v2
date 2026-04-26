package com.danydandy.SalonSpa.domain.ports.in;

import com.danydandy.SalonSpa.domain.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserUseCase {
    Flux<User> findAll();
    Mono<User> findById(Long id);
    Mono<User> update(Long id, User user);
    Mono<Void> delete(Long id);
    Flux<User> findBySalonId();
}
