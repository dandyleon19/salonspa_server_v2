package com.danydandy.SalonSpa.application.dto.request;

import com.danydandy.SalonSpa.domain.model.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateClientRequest {

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100, message = "Los nombres admiten como máximo 100 caracteres")
    private String firstName;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Los apellidos admiten como máximo 100 caracteres")
    private String lastName;

    @Size(max = 20, message = "El número de documento admite como máximo 20 caracteres")
    private String documentNumber;

    @Size(max = 20, message = "El teléfono admite como máximo 20 caracteres")
    private String phone;

    @Email(message = "El correo no es válido")
    @Size(max = 100, message = "El correo admite como máximo 100 caracteres")
    private String email;

    private LocalDate birthDate;

    private Gender gender;
}
