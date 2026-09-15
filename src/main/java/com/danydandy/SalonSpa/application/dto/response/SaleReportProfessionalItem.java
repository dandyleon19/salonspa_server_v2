package com.danydandy.SalonSpa.application.dto.response;

import java.math.BigDecimal;

public record SaleReportProfessionalItem(
        Long userId,
        String userName,
        long itemsSold,
        BigDecimal revenue
) {
}
