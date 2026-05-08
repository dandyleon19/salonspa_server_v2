package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.domain.model.User;
import com.danydandy.SalonSpa.domain.ports.out.UserRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Mono<User> save(User user) {
        return userRepository.save(userMapper.toEntity(user))
                .map(userMapper::toDomain);
    }

    @Override
    public Flux<User> findAll() {
        return userRepository.findAllByOrderByUpdatedAtAsc()
                .map(userMapper::toDomain);
    }

    @Override
    public Mono<User> findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return userRepository.deleteById(id);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDomain);
    }

    @Override
    public Flux<User> findBySalonId(Long id) {
        return userRepository.findBySalonIdOrderByUpdatedAtAsc(id)
                .map(userMapper::toDomain);
    }
}
