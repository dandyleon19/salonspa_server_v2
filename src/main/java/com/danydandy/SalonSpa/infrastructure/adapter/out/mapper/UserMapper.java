package com.danydandy.SalonSpa.infrastructure.adapter.out.mapper;

import com.danydandy.SalonSpa.application.dto.response.UserResponse;
import com.danydandy.SalonSpa.domain.model.User;
import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toDomain(UserEntity entity);
    UserEntity toEntity(User domain);

    @Mapping(
            target = "fullName",
            expression = "java(domain.getFirstName() + \" \" + domain.getLastName())"
    )
    @Mapping(target = "salonName", source = "salon.name")
    UserResponse toResponse(User domain);
}
