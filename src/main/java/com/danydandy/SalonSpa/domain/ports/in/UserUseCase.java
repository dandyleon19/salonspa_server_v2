package com.danydandy.SalonSpa.domain.ports.in;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.domain.model.Role;
import com.danydandy.SalonSpa.domain.model.User;
import reactor.core.publisher.Mono;

public interface UserUseCase {
    Mono<PageResponse<User>> findPage(int page, int size, Boolean isActive, Role role, String search);
    Mono<User> findById(Long id);
    Mono<User> update(Long id, User user);
    /** Sets a new password for the user and ends all their open sessions. */
    Mono<Void> resetPassword(Long id, String newPassword);
    Mono<Void> delete(Long id);
}
