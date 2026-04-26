package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.domain.model.User;
import com.danydandy.SalonSpa.domain.ports.out.UserRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository userRepository;

    private User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .isActive(entity.getIsActive())
                .commissionPercentage(entity.getCommissionPercentage())
                .salonId(entity.getSalonId())
                .build();
    }

    private UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(user.getPassword())
                .isActive(user.getIsActive())
                .commissionPercentage(user.getCommissionPercentage())
                .salonId(user.getSalonId())
                .build();
    }

    @Override
    public Mono<User> save(User user) {
        return userRepository.save(toEntity(user))
                .map(this::toDomain);
    }

    @Override
    public Flux<User> findAll() {
        return userRepository.findAll()
                .map(this::toDomain);
    }

    @Override
    public Mono<User> findById(Long id) {
        return userRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return userRepository.deleteById(id);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::toDomain);
    }

    @Override
    public Flux<User> findBySalonId(Long id) {
        return userRepository.findBySalonId(id)
                .map(this::toDomain);
    }
}
