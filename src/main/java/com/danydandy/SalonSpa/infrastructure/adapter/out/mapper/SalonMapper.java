package com.danydandy.SalonSpa.infrastructure.adapter.out.mapper;

import com.danydandy.SalonSpa.domain.model.Salon;
import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.SalonEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SalonMapper {

    Salon toDomain(SalonEntity entity);
    SalonEntity toEntity(Salon domain);
}
