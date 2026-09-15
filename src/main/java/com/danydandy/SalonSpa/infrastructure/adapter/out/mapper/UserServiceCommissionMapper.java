package com.danydandy.SalonSpa.infrastructure.adapter.out.mapper;

import com.danydandy.SalonSpa.application.dto.response.UserServiceCommissionResponse;
import com.danydandy.SalonSpa.domain.model.UserServiceCommission;
import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.UserServiceCommissionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserServiceCommissionMapper {

    @Mapping(target = "serviceName", ignore = true)
    UserServiceCommission toDomain(UserServiceCommissionEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserServiceCommissionEntity toEntity(UserServiceCommission domain);

    UserServiceCommissionResponse toResponse(UserServiceCommission domain);
}
