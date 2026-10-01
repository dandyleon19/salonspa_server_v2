package com.danydandy.SalonSpa.infrastructure.adapter.out.mapper;

import com.danydandy.SalonSpa.application.dto.response.ProductCategoryResponse;
import com.danydandy.SalonSpa.domain.model.ProductCategory;
import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.ProductCategoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface ProductCategoryMapper {
    ProductCategory toDomain(ProductCategoryEntity entity);
    ProductCategoryEntity toEntity(ProductCategory domain);
    ProductCategoryResponse toResponse(ProductCategory domain);
}
