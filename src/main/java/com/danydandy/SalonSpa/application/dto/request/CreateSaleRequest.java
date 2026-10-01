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

    @NotNull(message = "Client id is required")
    @Positive(message = "Client id must be positive")
    private Long clientId;

    @NotNull(message = "Branch id is required")
    @Positive(message = "Branch id must be positive")
    private Long branchId;

    @Positive(message = "Appointment id must be positive")
    private Long appointmentId;

    @PositiveOrZero(message = "Discount amount must be zero or positive")
    private BigDecimal discountAmount;

    private String notes;

    private LocalDateTime soldAt;

    @NotEmpty(message = "At least one sale item is required")
    @Valid
    private List<CreateSaleItemRequest> items;

    @Valid
    private List<CreateSalePaymentRequest> payments;
}
