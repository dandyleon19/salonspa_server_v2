package com.danydandy.SalonSpa.application.dto.request;

import com.danydandy.SalonSpa.domain.model.AppointmentStatus;
import com.danydandy.SalonSpa.domain.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAppointmentRequest {

    @Positive(message = "El profesional no es válido")
    private Long userId;

    @Positive(message = "La sucursal no es válida")
    private Long branchId;

    @Positive(message = "El servicio no es válido")
    private Long serviceId;

    private LocalDateTime startAt;

    private AppointmentStatus status;

    private String notes;

    private String cancellationReason;

    @Positive(message = "El adelanto debe ser mayor a 0")
    private BigDecimal depositAmount;

    private PaymentMethod depositPaymentMethod;
}
