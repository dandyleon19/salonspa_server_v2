package com.danydandy.SalonSpa.application.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBranchRequest {

    @Size(max = 50, message = "El nombre admite como máximo 50 caracteres")
    private String name;

    @Size(max = 100, message = "La dirección admite como máximo 100 caracteres")
    private String address;

    @Size(max = 100, message = "La ciudad admite como máximo 100 caracteres")
    private String city;

    @Positive(message = "El salón no es válido")
    private Long salonId;
}
