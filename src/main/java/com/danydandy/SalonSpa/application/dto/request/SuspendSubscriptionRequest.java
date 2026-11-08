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
public class SuspendSubscriptionRequest {

    @Size(max = 300, message = "El motivo no puede superar los 300 caracteres")
    private String reason;
}
