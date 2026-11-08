package com.danydandy.SalonSpa.application.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSaleItemRequest {

    @Positive(message = "El servicio no es válido")
    private Long serviceId;

    @Positive(message = "El producto no es válido")
    private Long productId;

    @Positive(message = "El profesional no es válido")
    private Long userId;

    @Positive(message = "La cita no es válida")
    private Long appointmentId;

    @Positive(message = "La cantidad debe ser mayor a 0")
    private Integer quantity;

    @PositiveOrZero(message = "El precio unitario no puede ser negativo")
    private BigDecimal unitPrice;

    @PositiveOrZero(message = "El descuento no puede ser negativo")
    private BigDecimal discountAmount;
}
