package com.danydandy.SalonSpa.application.dto.response;

import java.math.BigDecimal;

public record CommissionReportUserServiceItem(
        Long userId,
        String userName,
        Long serviceId,
        String serviceName,
        long quantity,
        BigDecimal revenue,
        Double appliedCommissionPercentage,
        boolean usedServiceCommission,
        BigDecimal commissionAmount
) {
}
