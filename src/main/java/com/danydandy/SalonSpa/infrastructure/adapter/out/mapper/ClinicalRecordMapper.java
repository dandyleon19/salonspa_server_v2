package com.danydandy.SalonSpa.infrastructure.adapter.out.mapper;

import com.danydandy.SalonSpa.domain.model.ClinicalRecord;
import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.ClinicalRecordEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClinicalRecordMapper {
    ClinicalRecord toDomain(ClinicalRecordEntity entity);
    ClinicalRecordEntity toEntity(ClinicalRecord domain);
}
