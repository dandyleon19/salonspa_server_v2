package com.danydandy.SalonSpa.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CommissionReportResponse(
        LocalDate from,
        LocalDate to,
        long totalItems,
        BigDecimal totalRevenue,
        BigDecimal totalCommission,
        List<CommissionReportUserItem> byUser,
        List<CommissionReportUserServiceItem> byUserAndService
) {
}
