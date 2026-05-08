package com.danydandy.SalonSpa.infrastructure.adapter.out.mapper;

import com.danydandy.SalonSpa.application.dto.response.BranchResponse;
import com.danydandy.SalonSpa.domain.model.Branch;
import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.BranchEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BranchMapper {
    Branch toDomain(BranchEntity entity);
    BranchEntity toEntity(Branch domain);
    @Mapping(target = "salonName", source = "salon.name")
    BranchResponse toResponse(Branch domain);
}
