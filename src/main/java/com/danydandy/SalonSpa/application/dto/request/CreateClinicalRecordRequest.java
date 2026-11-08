package com.danydandy.SalonSpa.application.dto.request;

import jakarta.validation.Valid;
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

    @NotNull(message = "El cliente es obligatorio")
    @Positive(message = "El cliente no es válido")
    private Long clientId;

    @NotNull(message = "El profesional es obligatorio")
    @Positive(message = "El profesional no es válido")
    private Long userId;

    @Positive(message = "La sucursal no es válida")
    private Long branchId;

    @Positive(message = "El servicio no es válido")
    private Long serviceId;

    private String diagnosis;

    private String treatment;

    private String observations;

    private LocalDateTime sessionDate;

    @Valid
    private NextAppointmentRequest nextAppointment;
}
