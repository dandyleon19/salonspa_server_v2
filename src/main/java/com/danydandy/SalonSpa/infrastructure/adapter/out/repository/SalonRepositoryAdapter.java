package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.domain.model.Salon;
import com.danydandy.SalonSpa.domain.ports.out.SalonRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.SalonEntity;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.SalonMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SalonRepositoryAdapter implements SalonRepositoryPort {

    private final SalonRepository salonRepository;
    private final SalonMapper salonMapper;

    @Override
    public Mono<Salon> save(Salon salon) {
        return salonRepository.save(salonMapper.toEntity(salon))
                .map(salonMapper::toDomain);
    }

    @Override
    public Flux<Salon> findAll() {
        return salonRepository.findAll()
                .map(salonMapper::toDomain);
    }

    @Override
    public Mono<Salon> findById(Long id) {
        return salonRepository.findById(id)
                .map(salonMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return salonRepository.deleteById(id);
    }
}
