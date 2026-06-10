package com.danydandy.SalonSpa.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BootstrapRequest {

    @NotNull(message = "Salon data is required")
    @Valid
    private CreateSalonRequest salon;

    @NotNull(message = "Admin user data is required")
    @Valid
    private CreateUserRequest admin;
}
