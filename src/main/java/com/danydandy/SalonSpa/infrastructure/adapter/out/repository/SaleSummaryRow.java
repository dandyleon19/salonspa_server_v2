package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaleSummaryRow {
    private Long totalSales;
    private BigDecimal totalRevenue;
    private BigDecimal totalPaid;
    private BigDecimal totalOutstanding;
}
