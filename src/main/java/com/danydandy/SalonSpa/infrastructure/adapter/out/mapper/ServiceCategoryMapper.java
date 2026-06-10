package com.danydandy.SalonSpa.infrastructure.adapter.out.mapper;

import com.danydandy.SalonSpa.application.dto.response.ServiceCategoryResponse;
import com.danydandy.SalonSpa.domain.model.ServiceCategory;
import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.ServiceCategoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ServiceMapper.class)
public interface ServiceCategoryMapper {
    ServiceCategory toDomain(ServiceCategoryEntity entity);
    ServiceCategoryEntity toEntity(ServiceCategory domain);
    ServiceCategoryResponse toResponse(ServiceCategory domain);
}
