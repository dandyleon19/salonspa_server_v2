package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.domain.model.Service;
import com.danydandy.SalonSpa.domain.ports.out.ServiceRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.ServiceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
    public Flux<Service> findAll(int page, int size) {
        return serviceRepository.findAllByOrderByCreatedAtAsc(PageRequest.of(page, size))
                .map(serviceMapper::toDomain);
    }

    @Override
    public Mono<Long> countAll() {
        return serviceRepository.count();
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
    public Flux<Service> findBySalonId(Long salonId, int page, int size) {
        return serviceRepository.findBySalonIdOrderByCreatedAtAsc(salonId, PageRequest.of(page, size))
                .map(serviceMapper::toDomain);
    }

    @Override
    public Mono<Long> countBySalonId(Long salonId) {
        return serviceRepository.countBySalonId(salonId);
    }

    @Override
    public Flux<Service> findByCategoryId(Long id) {
        return serviceRepository.findByCategoryIdOrderByCreatedAtAsc(id)
                .map(serviceMapper::toDomain);
    }
}
