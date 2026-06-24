package com.danydandy.SalonSpa.application.dto.request;

import com.danydandy.SalonSpa.domain.model.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentRequest {

    @NotNull(message = "Client id is required")
    @Positive(message = "Client id must be positive")
    private Long clientId;

    @NotNull(message = "User id is required")
    @Positive(message = "User id must be positive")
    private Long userId;

    @NotNull(message = "Branch id is required")
    @Positive(message = "Branch id must be positive")
    private Long branchId;

    @Positive(message = "Service id must be positive")
    private Long serviceId;

    @NotNull(message = "Start at is required")
    private LocalDateTime startAt;

    private String notes;
}
