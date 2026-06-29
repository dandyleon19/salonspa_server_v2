package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.RefreshTokenEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface RefreshTokenRepository extends R2dbcRepository<RefreshTokenEntity, Long> {

    @Query("""
            SELECT id, user_id, token_hash, expires_at, created_at, revoked_at
            FROM refresh_tokens
            WHERE token_hash = :tokenHash
              AND revoked_at IS NULL
              AND expires_at > CURRENT_TIMESTAMP
            """)
    Mono<RefreshTokenEntity> findValidByTokenHash(String tokenHash);
}
