package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.domain.model.User;
import com.danydandy.SalonSpa.domain.ports.out.UserRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
    public Flux<User> findAll(int page, int size) {
        return userRepository.findAllByOrderByCreatedAtAsc(PageRequest.of(page, size))
                .map(userMapper::toDomain);
    }

    @Override
    public Mono<Long> countAll() {
        return userRepository.count();
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
    public Flux<User> findBySalonId(Long salonId, int page, int size) {
        return userRepository.findBySalonIdOrderByCreatedAtAsc(salonId, PageRequest.of(page, size))
                .map(userMapper::toDomain);
    }

    @Override
    public Mono<Long> countBySalonId(Long salonId) {
        return userRepository.countBySalonId(salonId);
    }
}
