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
public class UpdateAppointmentRequest {

    @Positive(message = "User id must be positive")
    private Long userId;

    @Positive(message = "Branch id must be positive")
    private Long branchId;

    @Positive(message = "Service id must be positive")
    private Long serviceId;

    private LocalDateTime startAt;

    private AppointmentStatus status;

    private String notes;

    private String cancellationReason;
}
