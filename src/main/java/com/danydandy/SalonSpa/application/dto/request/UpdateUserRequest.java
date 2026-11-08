package com.danydandy.SalonSpa.application.dto.request;

import com.danydandy.SalonSpa.domain.model.Role;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 255, message = "Los nombres admiten como máximo 255 caracteres")
    private String firstName;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 255, message = "Los apellidos admiten como máximo 255 caracteres")
    private String lastName;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no es válido")
    @Size(max = 255, message = "El correo admite como máximo 255 caracteres")
    private String email;

    private Boolean isActive;

    @NotNull(message = "El rol es obligatorio")
    private Role role;

    @DecimalMin(value = "0.0", message = "El porcentaje de comisión no puede ser menor a 0")
    @DecimalMax(value = "100.0", message = "El porcentaje de comisión no puede superar 100")
    private Double commissionPercentage;
}
