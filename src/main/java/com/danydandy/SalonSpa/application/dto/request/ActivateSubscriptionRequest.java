package com.danydandy.SalonSpa.application.dto.request;

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
public class ActivateSubscriptionRequest {

    @NotBlank(message = "El plan es obligatorio")
    @Size(max = 30, message = "El plan no puede superar los 30 caracteres")
    private String planCode;

    /** The day the salon is paid until (yyyy-MM-dd). Required unless {@code noExpiry} is true. */
    private LocalDate paidUntil;

    /** True = the plan never expires (a salon that does not pay); paidUntil is then ignored. */
    private Boolean noExpiry;
}
