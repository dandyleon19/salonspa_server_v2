package com.danydandy.SalonSpa.infrastructure.adapter.out.mapper;

import com.danydandy.SalonSpa.application.dto.response.ProductResponse;
import com.danydandy.SalonSpa.domain.model.Product;
import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toDomain(ProductEntity entity);
    ProductEntity toEntity(Product domain);
    ProductResponse toResponse(Product domain);
}
