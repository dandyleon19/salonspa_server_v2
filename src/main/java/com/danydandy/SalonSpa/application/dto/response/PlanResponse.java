package com.danydandy.SalonSpa.application.dto.response;

import com.danydandy.SalonSpa.domain.model.Plan;

import java.math.BigDecimal;
import java.util.List;

public record PlanResponse(
        String code,
        String name,
        BigDecimal priceMonthly,
        Integer maxBranches,
        Integer maxUsers,
        List<String> features
) {
    public static PlanResponse from(Plan plan) {
        return new PlanResponse(
                plan.getCode(),
                plan.getName(),
                plan.getPriceMonthly(),
                plan.getMaxBranches(),
                plan.getMaxUsers(),
                plan.getFeatures() == null ? List.of() : plan.getFeatures().stream().sorted().toList()
        );
    }
}
