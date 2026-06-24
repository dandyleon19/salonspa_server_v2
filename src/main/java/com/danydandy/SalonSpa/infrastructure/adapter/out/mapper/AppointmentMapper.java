package com.danydandy.SalonSpa.infrastructure.adapter.out.mapper;

import com.danydandy.SalonSpa.application.dto.response.AppointmentResponse;
import com.danydandy.SalonSpa.domain.model.Appointment;
import com.danydandy.SalonSpa.domain.model.AppointmentStatus;
import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.AppointmentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(target = "status", source = "status", qualifiedByName = "stringToStatus")
    Appointment toDomain(AppointmentEntity entity);

    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    AppointmentEntity toEntity(Appointment domain);

    @Mapping(target = "status", source = "status", qualifiedByName = "statusToResponseString")
    AppointmentResponse toResponse(Appointment domain);

    @Named("stringToStatus")
    default AppointmentStatus stringToStatus(String status) {
        return status != null ? AppointmentStatus.valueOf(status) : null;
    }

    @Named("statusToString")
    default String statusToString(AppointmentStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("statusToResponseString")
    default String statusToResponseString(AppointmentStatus status) {
        return status != null ? status.name() : null;
    }
}
