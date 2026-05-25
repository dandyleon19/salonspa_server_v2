package com.danydandy.SalonSpa.infrastructure.adapter.out.mapper;

import com.danydandy.SalonSpa.domain.model.ClinicalRecordService;
import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.ClinicalRecordServiceEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClinicalRecordServiceMapper {
    ClinicalRecordService toDomain(ClinicalRecordServiceEntity entity);
    ClinicalRecordServiceEntity toEntity(ClinicalRecordService domain);
}
