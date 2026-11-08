package com.danydandy.SalonSpa.domain.ports.out;

import com.danydandy.SalonSpa.domain.model.RefreshToken;
import reactor.core.publisher.Mono;

public interface RefreshTokenRepositoryPort {

    Mono<RefreshToken> save(RefreshToken refreshToken);

    Mono<RefreshToken> findValidByTokenHash(String tokenHash);

    Mono<Void> revokeById(Long id);

    /** Ends every open session of the user (their refresh tokens stop working). */
    Mono<Void> revokeAllByUserId(Long userId);
}
