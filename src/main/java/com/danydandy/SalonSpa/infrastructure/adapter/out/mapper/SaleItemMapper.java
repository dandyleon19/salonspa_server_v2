package com.danydandy.SalonSpa.infrastructure.adapter.out.mapper;

import com.danydandy.SalonSpa.application.dto.response.SaleItemResponse;
import com.danydandy.SalonSpa.domain.model.SaleItem;
import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.SaleItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SaleItemMapper {

    @Mapping(target = "userName", ignore = true)
    SaleItem toDomain(SaleItemEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    SaleItemEntity toEntity(SaleItem domain);

    SaleItemResponse toResponse(SaleItem domain);
}
