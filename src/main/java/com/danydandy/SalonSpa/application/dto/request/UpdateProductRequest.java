package com.danydandy.SalonSpa.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name must be at most 150 characters")
    private String name;

    private String description;

    private String longDescription;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be zero or greater")
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @PositiveOrZero(message = "Stock quantity must be zero or greater")
    private Integer stockQuantity;

    @PositiveOrZero(message = "Low stock threshold must be zero or greater")
    private Integer lowStockThreshold;

    private Boolean isActive;
}
