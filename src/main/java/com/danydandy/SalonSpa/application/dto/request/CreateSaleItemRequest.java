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

    @Positive(message = "Service id must be positive")
    private Long serviceId;

    @Positive(message = "Product id must be positive")
    private Long productId;

    @Positive(message = "User id must be positive")
    private Long userId;

    @Positive(message = "Appointment id must be positive")
    private Long appointmentId;

    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    @PositiveOrZero(message = "Unit price must be zero or positive")
    private BigDecimal unitPrice;

    @PositiveOrZero(message = "Discount amount must be zero or positive")
    private BigDecimal discountAmount;
}
