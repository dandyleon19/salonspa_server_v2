package com.danydandy.SalonSpa.domain.ports.in;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.domain.model.User;
import reactor.core.publisher.Mono;

public interface UserUseCase {
    Mono<PageResponse<User>> findPage(int page, int size);
    Mono<User> findById(Long id);
    Mono<User> update(Long id, User user);
    Mono<Void> delete(Long id);
}
