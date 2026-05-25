package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.domain.model.ClinicalRecordService;
import com.danydandy.SalonSpa.domain.ports.out.ClinicalRecordServiceRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.ClinicalRecordServiceMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ClinicalRecordServiceRepositoryAdapter implements ClinicalRecordServiceRepositoryPort {

    private final ClinicalRecordServiceRepository clinicalRecordServiceRepository;
    private final ClinicalRecordServiceMapper clinicalRecordServiceMapper;

    @Override
    public Mono<ClinicalRecordService> save(ClinicalRecordService clinicalRecordService) {
        return clinicalRecordServiceRepository.save(clinicalRecordServiceMapper.toEntity(clinicalRecordService))
                .map(clinicalRecordServiceMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return clinicalRecordServiceRepository.deleteById(id);
    }

    @Override
    public Flux<ClinicalRecordService> findByClinicalRecordId(Long id) {
        return clinicalRecordServiceRepository.findByClinicalRecordId(id)
                .map(clinicalRecordServiceMapper::toDomain);
    }
}
