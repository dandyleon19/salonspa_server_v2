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
public class NextAppointmentRequest {

    @NotNull(message = "El profesional es obligatorio")
    @Positive(message = "El profesional no es válido")
    private Long userId;

    @NotNull(message = "La sucursal es obligatoria")
    @Positive(message = "La sucursal no es válida")
    private Long branchId;

    @NotNull(message = "La fecha y hora de inicio son obligatorias")
    private LocalDateTime startAt;

    @Positive(message = "El servicio no es válido")
    private Long serviceId;

    private String notes;
}
