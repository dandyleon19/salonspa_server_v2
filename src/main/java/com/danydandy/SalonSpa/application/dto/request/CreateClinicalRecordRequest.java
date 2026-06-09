package com.danydandy.SalonSpa.application.dto.request;

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
public class CreateClinicalRecordRequest {

    @NotNull(message = "Client id is required")
    @Positive(message = "Client id must be positive")
    private Long clientId;

    @NotNull(message = "User id is required")
    @Positive(message = "User id must be positive")
    private Long userId;

    @Positive(message = "Branch id must be positive")
    private Long branchId;

    @Positive(message = "Service id must be positive")
    private Long serviceId;

    private String diagnosis;

    private String treatment;

    private String observations;

    private LocalDateTime sessionDate;
}
