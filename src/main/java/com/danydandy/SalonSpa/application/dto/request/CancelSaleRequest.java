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

    @Size(max = 500, message = "Cancellation reason must be at most 500 characters")
    private String reason;
}
