package com.danydandy.SalonSpa.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplaceUserServiceCommissionsRequest {

    @Valid
    @NotNull(message = "Falta la lista de comisiones")
    private List<UserServiceCommissionItemRequest> commissions;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserServiceCommissionItemRequest {

        @NotNull(message = "El servicio es obligatorio")
        @Positive(message = "El servicio no es válido")
        private Long serviceId;

        @NotNull(message = "El porcentaje de comisión es obligatorio")
        @DecimalMin(value = "0.0", message = "El porcentaje de comisión no puede ser menor a 0")
        @DecimalMax(value = "100.0", message = "El porcentaje de comisión no puede superar 100")
        private Double commissionPercentage;
    }
}
