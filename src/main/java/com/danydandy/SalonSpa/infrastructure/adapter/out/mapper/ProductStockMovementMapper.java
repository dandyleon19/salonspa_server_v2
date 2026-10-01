package com.danydandy.SalonSpa.infrastructure.adapter.out.mapper;

import com.danydandy.SalonSpa.application.dto.response.ProductStockMovementResponse;
import com.danydandy.SalonSpa.domain.model.ProductStockMovement;
import com.danydandy.SalonSpa.domain.model.StockMovementType;
import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.ProductStockMovementEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ProductStockMovementMapper {

    @Mapping(target = "movementType", source = "movementType", qualifiedByName = "stringToMovementType")
    @Mapping(target = "createdByUserName", ignore = true)
    ProductStockMovement toDomain(ProductStockMovementEntity entity);

    @Mapping(target = "movementType", source = "movementType", qualifiedByName = "movementTypeToString")
    ProductStockMovementEntity toEntity(ProductStockMovement domain);

    ProductStockMovementResponse toResponse(ProductStockMovement domain);

    @Named("stringToMovementType")
    default StockMovementType stringToMovementType(String value) {
        return value != null ? StockMovementType.valueOf(value) : null;
    }

    @Named("movementTypeToString")
    default String movementTypeToString(StockMovementType type) {
        return type != null ? type.name() : null;
    }
}
