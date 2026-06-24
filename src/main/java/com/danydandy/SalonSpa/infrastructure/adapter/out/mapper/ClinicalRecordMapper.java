package com.danydandy.SalonSpa.infrastructure.adapter.out.mapper;

import com.danydandy.SalonSpa.application.dto.response.ClinicalRecordResponse;
import com.danydandy.SalonSpa.domain.model.ClinicalRecord;
import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.ClinicalRecordEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = AppointmentMapper.class)
public interface ClinicalRecordMapper {
    ClinicalRecord toDomain(ClinicalRecordEntity entity);
    ClinicalRecordEntity toEntity(ClinicalRecord domain);
    ClinicalRecordResponse toResponse(ClinicalRecord domain);
}
