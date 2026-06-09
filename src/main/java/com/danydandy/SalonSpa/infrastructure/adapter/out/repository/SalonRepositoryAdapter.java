package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.domain.model.Salon;
import com.danydandy.SalonSpa.domain.ports.out.SalonRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.SalonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
    public Flux<Salon> findAll(int page, int size) {
        return salonRepository.findAllByOrderByCreatedAtAsc(PageRequest.of(page, size))
                .map(salonMapper::toDomain);
    }

    @Override
    public Mono<Long> countAll() {
        return salonRepository.count();
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
