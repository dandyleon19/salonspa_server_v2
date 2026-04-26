package com.danydandy.SalonSpa.infrastructure.adapter.out.mapper;

import com.danydandy.SalonSpa.domain.model.Client;
import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.ClientEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    Client toDomain(ClientEntity entity);
    ClientEntity toEntity(Client domain);
}
