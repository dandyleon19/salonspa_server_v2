package com.danydandy.SalonSpa.domain.ports.out;

import com.danydandy.SalonSpa.domain.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepositoryPort {
    Mono<User> save(User user);
    Flux<User> findAll(int page, int size);
    Mono<Long> countAll();
    Mono<User> findById(Long id);
    Mono<Void> deleteById(Long id);
    Mono<User> findByEmail(String email);
    Flux<User> findBySalonId(Long salonId, int page, int size);
    Mono<Long> countBySalonId(Long salonId);
}
