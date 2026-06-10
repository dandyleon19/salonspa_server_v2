package com.danydandy.SalonSpa.infrastructure.adapter.out.mapper;

import com.danydandy.SalonSpa.application.dto.response.ClientResponse;
import com.danydandy.SalonSpa.domain.model.Client;
import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.ClientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    Client toDomain(ClientEntity entity);
    ClientEntity toEntity(Client domain);

    @Mapping(
            target = "fullName",
            expression = "java(domain.getFirstName() + \" \" + domain.getLastName())"
    )
    ClientResponse toResponse(Client domain);
}
