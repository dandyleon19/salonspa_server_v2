package com.danydandy.SalonSpa.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSaleRequest {

    @NotNull(message = "El cliente es obligatorio")
    @Positive(message = "El cliente no es válido")
    private Long clientId;

    @NotNull(message = "La sucursal es obligatoria")
    @Positive(message = "La sucursal no es válida")
    private Long branchId;

    @Positive(message = "La cita no es válida")
    private Long appointmentId;

    @PositiveOrZero(message = "El descuento no puede ser negativo")
    private BigDecimal discountAmount;

    private String notes;

    private LocalDateTime soldAt;

    @NotEmpty(message = "Se necesita al menos un ítem en la venta")
    @Valid
    private List<CreateSaleItemRequest> items;

    @Valid
    private List<CreateSalePaymentRequest> payments;
}
