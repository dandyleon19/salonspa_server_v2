package com.danydandy.SalonSpa.infrastructure.adapter.out.mapper;

import com.danydandy.SalonSpa.application.dto.response.SalonResponse;
import com.danydandy.SalonSpa.domain.model.Salon;
import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.SalonEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = BranchMapper.class)
public interface SalonMapper {
    Salon toDomain(SalonEntity entity);
    SalonEntity toEntity(Salon domain);
    SalonResponse toResponse(Salon domain);
}
