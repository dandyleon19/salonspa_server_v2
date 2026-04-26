package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.domain.model.AuthUser;
import com.danydandy.SalonSpa.domain.model.User;
import com.danydandy.SalonSpa.domain.ports.in.UserUseCase;
import com.danydandy.SalonSpa.domain.ports.out.SalonRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserService implements UserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final SalonRepositoryPort salonRepositoryPort;

    @Override
    public Flux<User> findAll() {
        return userRepositoryPort.findAll()
                .flatMap(user -> salonRepositoryPort.findById(user.getSalonId())
                        .map(salon -> {
                            user.setSalon(salon);
                            return user;
                        }));
    }

    @Override
    public Mono<User> findById(Long id) {
        return userRepositoryPort.findById(id)
                .flatMap(user -> salonRepositoryPort.findById(user.getSalonId())
                        .map(salon -> {
                            user.setSalon(salon);
                            return user;
                        }));
    }

    @Override
    public Mono<User> update(Long id, User user) {
        return userRepositoryPort.findById(id)
                .flatMap(existing -> {
                    existing.setFirstName(user.getFirstName());
                    existing.setLastName(user.getLastName());
                    existing.setEmail(user.getEmail());
                    existing.setIsActive(user.getIsActive());
                    existing.setCommissionPercentage(user.getCommissionPercentage());
                    return userRepositoryPort.save(existing);
                });
    }

    @Override
    public Mono<Void> delete(Long id) {
        return userRepositoryPort.deleteById(id);
    }

    @Override
    public Flux<User> findBySalonId() {
        // return userRepositoryPort.findBySalonId(id);
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (AuthUser) ctx.getAuthentication().getPrincipal())
                .flatMapMany(authUser ->
                        userRepositoryPort.findBySalonId(authUser.getSalonId())
                );
    }
}
