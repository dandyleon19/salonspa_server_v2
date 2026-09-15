package com.danydandy.SalonSpa.application.dto.response;

import java.math.BigDecimal;

public record CommissionReportUserItem(
        Long userId,
        String userName,
        Double defaultCommissionPercentage,
        long itemsSold,
        BigDecimal revenue,
        BigDecimal commissionAmount
) {
}
