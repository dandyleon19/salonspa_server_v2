package com.danydandy.SalonSpa.infrastructure.adapter.out.mapper;

import com.danydandy.SalonSpa.application.dto.response.SaleResponse;
import com.danydandy.SalonSpa.domain.model.Sale;
import com.danydandy.SalonSpa.domain.model.SaleStatus;
import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.SaleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {SaleItemMapper.class, SalePaymentMapper.class})
public interface SaleMapper {

    @Mapping(target = "status", source = "status", qualifiedByName = "stringToStatus")
    @Mapping(target = "clientName", ignore = true)
    @Mapping(target = "branchName", ignore = true)
    @Mapping(target = "registeredByUserName", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "payments", ignore = true)
    Sale toDomain(SaleEntity entity);

    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    SaleEntity toEntity(Sale domain);

    @Mapping(target = "status", source = "status", qualifiedByName = "statusToResponseString")
    SaleResponse toResponse(Sale domain);

    @Named("stringToStatus")
    default SaleStatus stringToStatus(String status) {
        return status != null ? SaleStatus.valueOf(status) : null;
    }

    @Named("statusToString")
    default String statusToString(SaleStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("statusToResponseString")
    default String statusToResponseString(SaleStatus status) {
        return status != null ? status.name() : null;
    }
}
