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
public class CreateServiceRequest {

    @NotNull(message = "La categoría es obligatoria")
    @Positive(message = "La categoría no es válida")
    private Long categoryId;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre admite como máximo 150 caracteres")
    private String name;

    private String description;

    private String longDescription;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
    private BigDecimal price;

    @Positive(message = "La duración debe ser mayor a 0 minutos")
    private Integer durationMinutes;

    private Boolean isActive;
}
