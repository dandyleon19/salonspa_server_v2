package com.danydandy.SalonSpa.application.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CancelSaleRequest {

    @Size(max = 500, message = "El motivo de cancelación admite como máximo 500 caracteres")
    private String reason;
}
