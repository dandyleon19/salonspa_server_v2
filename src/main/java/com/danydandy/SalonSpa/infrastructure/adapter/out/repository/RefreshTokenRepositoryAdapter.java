package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.domain.model.RefreshToken;
import com.danydandy.SalonSpa.domain.ports.out.RefreshTokenRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.RefreshTokenMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenMapper refreshTokenMapper;

    @Override
    public Mono<RefreshToken> save(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshTokenMapper.toEntity(refreshToken))
                .map(refreshTokenMapper::toDomain);
    }

    @Override
    public Mono<RefreshToken> findValidByTokenHash(String tokenHash) {
        return refreshTokenRepository.findValidByTokenHash(tokenHash)
                .map(refreshTokenMapper::toDomain);
    }

    @Override
    public Mono<Void> revokeById(Long id) {
        return refreshTokenRepository.findById(id)
                .flatMap(entity -> {
                    entity.setRevokedAt(LocalDateTime.now());
                    return refreshTokenRepository.save(entity);
                })
                .then();
    }
}
