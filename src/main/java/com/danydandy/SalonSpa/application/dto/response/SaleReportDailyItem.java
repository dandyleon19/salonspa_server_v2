package com.danydandy.SalonSpa.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SaleReportDailyItem(
        LocalDate date,
        long salesCount,
        BigDecimal revenue
) {
}
