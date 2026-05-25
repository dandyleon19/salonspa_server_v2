package com.danydandy.SalonSpa.infrastructure.adapter.out.mapper;

import com.danydandy.SalonSpa.domain.model.Service;
import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.ServiceEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ServiceMapper {
    Service toDomain(ServiceEntity entity);
    ServiceEntity toEntity(Service domain);
}
