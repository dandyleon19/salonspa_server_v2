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

    @Size(max = 300, message = "Reason must be at most 300 characters")
    private String reason;
}
