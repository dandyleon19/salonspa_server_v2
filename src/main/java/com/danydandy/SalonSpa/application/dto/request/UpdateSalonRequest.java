package com.danydandy.SalonSpa.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSalonRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @Size(max = 100, message = "Social reason must be at most 100 characters")
    private String socialReason;

    @Size(max = 100, message = "Fiscal address must be at most 100 characters")
    private String fiscalAddress;

    @Size(max = 20, message = "RUC number must be at most 20 characters")
    private String rucNumber;

    @Size(max = 20, message = "Phone must be at most 20 characters")
    private String phone;
}
