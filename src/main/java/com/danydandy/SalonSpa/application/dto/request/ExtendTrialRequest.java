package com.danydandy.SalonSpa.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExtendTrialRequest {

    @NotNull(message = "Los días son obligatorios")
    @Min(value = 1, message = "Los días deben ser al menos 1")
    @Max(value = 365, message = "Los días no pueden superar 365")
    private Integer days;
}
