package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.domain.model.ClinicalRecord;
import com.danydandy.SalonSpa.domain.ports.out.ClinicalRecordRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.ClinicalRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
    public Flux<ClinicalRecord> findAll(int page, int size) {
        return clinicalRecordRepository.findAllByOrderByCreatedAtAsc(PageRequest.of(page, size))
                .map(clinicalRecordMapper::toDomain);
    }

    @Override
    public Mono<Long> countAll() {
        return clinicalRecordRepository.count();
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
    public Flux<ClinicalRecord> findByClientId(Long clientId, int page, int size) {
        return clinicalRecordRepository.findByClientIdOrderByCreatedAtAsc(clientId, PageRequest.of(page, size))
                .map(clinicalRecordMapper::toDomain);
    }

    @Override
    public Mono<Long> countByClientId(Long clientId) {
        return clinicalRecordRepository.countByClientId(clientId);
    }

    @Override
    public Flux<ClinicalRecord> findBySalonId(Long salonId, int page, int size) {
        long offset = (long) page * size;
        return clinicalRecordRepository.findPageBySalonId(salonId, size, offset)
                .map(clinicalRecordMapper::toDomain);
    }

    @Override
    public Mono<Long> countBySalonId(Long salonId) {
        return clinicalRecordRepository.countBySalonId(salonId);
    }
}
