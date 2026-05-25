package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.domain.model.Service;
import com.danydandy.SalonSpa.domain.ports.out.ServiceRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.ServiceMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ServiceRepositoryAdapter implements ServiceRepositoryPort {

    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;

    @Override
    public Mono<Service> save(Service service) {
        return serviceRepository.save(serviceMapper.toEntity(service))
                .map(serviceMapper::toDomain);
    }

    @Override
    public Flux<Service> findAll() {
        return serviceRepository.findAll()
                .map(serviceMapper::toDomain);
    }

    @Override
    public Mono<Service> findById(Long id) {
        return serviceRepository.findById(id)
                .map(serviceMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return serviceRepository.deleteById(id);
    }

    @Override
    public Flux<Service> findBySalonId(Long id) {
        return serviceRepository.findBySalonIdOrderByCreatedAtAsc(id)
                .map(serviceMapper::toDomain);
    }

    @Override
    public Flux<Service> findByCategoryId(Long id) {
        return serviceRepository.findByCategoryIdOrderByCreatedAtAsc(id)
                .map(serviceMapper::toDomain);
    }
}
