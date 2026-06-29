package com.danydandy.SalonSpa.infrastructure.adapter.out.mapper;

import com.danydandy.SalonSpa.application.dto.response.PublicServiceCategoryResponse;
import com.danydandy.SalonSpa.application.dto.response.PublicServiceResponse;
import com.danydandy.SalonSpa.domain.model.Service;
import com.danydandy.SalonSpa.domain.model.ServiceCategory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PublicCatalogMapper {
    PublicServiceCategoryResponse toResponse(ServiceCategory category);
    PublicServiceResponse toResponse(Service service);
}
