package com.danydandy.SalonSpa.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record SaleReportResponse(
        LocalDate from,
        LocalDate to,
        long totalSales,
        BigDecimal totalRevenue,
        BigDecimal totalPaid,
        BigDecimal totalOutstanding,
        Map<String, BigDecimal> revenueByPaymentMethod,
        List<SaleReportServiceItem> topServices,
        List<SaleReportProfessionalItem> byProfessional,
        List<SaleReportDailyItem> dailyBreakdown
) {
}
