package com.danydandy.SalonSpa.application.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpsertUserServiceCommissionRequest {

    @NotNull(message = "El porcentaje de comisión es obligatorio")
    @DecimalMin(value = "0.0", message = "El porcentaje de comisión no puede ser menor a 0")
    @DecimalMax(value = "100.0", message = "El porcentaje de comisión no puede superar 100")
    private Double commissionPercentage;
}
