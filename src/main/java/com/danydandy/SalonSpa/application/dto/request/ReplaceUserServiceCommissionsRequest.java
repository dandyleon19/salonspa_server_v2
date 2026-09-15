package com.danydandy.SalonSpa.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplaceUserServiceCommissionsRequest {

    @Valid
    @NotNull(message = "Commissions list is required")
    private List<UserServiceCommissionItemRequest> commissions;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserServiceCommissionItemRequest {

        @NotNull(message = "Service id is required")
        @Positive(message = "Service id must be positive")
        private Long serviceId;

        @NotNull(message = "Commission percentage is required")
        @DecimalMin(value = "0.0", message = "Commission percentage must be at least 0")
        @DecimalMax(value = "100.0", message = "Commission percentage must be at most 100")
        private Double commissionPercentage;
    }
}
