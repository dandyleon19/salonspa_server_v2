package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.domain.model.ServiceCategory;
import com.danydandy.SalonSpa.domain.ports.out.ServiceCategoryRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.ServiceCategoryMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ServiceCategoryRepositoryAdapter implements ServiceCategoryRepositoryPort {

    private final ServiceCategoryRepository serviceCategoryRepository;
    private final ServiceCategoryMapper serviceCategoryMapper;

    @Override
    public Mono<ServiceCategory> save(ServiceCategory serviceCategory) {
        return serviceCategoryRepository.save(serviceCategoryMapper.toEntity(serviceCategory))
                .map(serviceCategoryMapper::toDomain);
    }

    @Override
    public Flux<ServiceCategory> findAll(int page, int size, String search) {
        long offset = (long) page * size;
        return serviceCategoryRepository.findPage(search, size, offset)
                .map(serviceCategoryMapper::toDomain);
    }

    @Override
    public Mono<Long> countAll(String search) {
        return serviceCategoryRepository.countFiltered(search);
    }

    @Override
    public Mono<ServiceCategory> findById(Long id) {
        return serviceCategoryRepository.findById(id)
                .map(serviceCategoryMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return serviceCategoryRepository.deleteById(id);
    }

    @Override
    public Flux<ServiceCategory> findBySalonId(Long salonId, int page, int size, String search) {
        long offset = (long) page * size;
        return serviceCategoryRepository.findPageBySalonId(salonId, search, size, offset)
                .map(serviceCategoryMapper::toDomain);
    }

    @Override
    public Mono<Long> countBySalonId(Long salonId, String search) {
        return serviceCategoryRepository.countBySalonId(salonId, search);
    }
}
