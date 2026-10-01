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

    @NotNull(message = "Movement type is required")
    private StockMovementType movementType;

    @NotNull(message = "Quantity delta is required")
    private Integer quantityDelta;

    @Size(max = 255, message = "Reason must be at most 255 characters")
    private String reason;
}
