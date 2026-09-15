package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CommissionSummaryRow {
    private Long totalItems;
    private BigDecimal totalRevenue;
    private BigDecimal totalCommission;
}
