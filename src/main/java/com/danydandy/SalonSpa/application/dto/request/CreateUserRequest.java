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
public class CreateUserRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 255, message = "First name must be at most 255 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 255, message = "Last name must be at most 255 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must be at most 255 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    private String password;

    private Role role;

    @Positive(message = "Salon id must be positive")
    private Long salonId;

    @DecimalMin(value = "0.0", message = "Commission percentage must be at least 0")
    @DecimalMax(value = "100.0", message = "Commission percentage must be at most 100")
    private Double commissionPercentage;
}
