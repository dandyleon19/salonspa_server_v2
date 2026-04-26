package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.domain.model.ClinicalRecord;
import com.danydandy.SalonSpa.domain.ports.out.ClinicalRecordRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.ClinicalRecordMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ClinicalRecordRepositoryAdapter implements ClinicalRecordRepositoryPort {

    private final ClinicalRecordRepository clinicalRecordRepository;
    private final ClinicalRecordMapper clinicalRecordMapper;

    @Override
    public Mono<ClinicalRecord> save(ClinicalRecord clinicalRecord) {
        return clinicalRecordRepository.save(clinicalRecordMapper.toEntity(clinicalRecord))
                .map(clinicalRecordMapper::toDomain);
    }

    @Override
    public Flux<ClinicalRecord> findAll() {
        return clinicalRecordRepository.findAll()
                .map(clinicalRecordMapper::toDomain);
    }

    @Override
    public Mono<ClinicalRecord> findById(Long id) {
        return clinicalRecordRepository.findById(id)
                .map(clinicalRecordMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return clinicalRecordRepository.deleteById(id);
    }

    @Override
    public Flux<ClinicalRecord> findByClientId(Long id) {
        return clinicalRecordRepository.findByClientId(id)
                .map(clinicalRecordMapper::toDomain);
    }
}
