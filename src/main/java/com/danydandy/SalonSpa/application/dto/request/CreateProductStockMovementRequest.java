package com.danydandy.SalonSpa.application.dto.request;

import com.danydandy.SalonSpa.domain.model.StockMovementType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductStockMovementRequest {

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private StockMovementType movementType;

    @NotNull(message = "La cantidad es obligatoria")
    private Integer quantityDelta;

    @Size(max = 255, message = "El motivo admite como máximo 255 caracteres")
    private String reason;
}
