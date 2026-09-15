package com.danydandy.SalonSpa.application.dto.response;

import java.math.BigDecimal;

public record SaleReportServiceItem(
        Long serviceId,
        String serviceName,
        long quantity,
        BigDecimal revenue
) {
}
