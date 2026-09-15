package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CommissionUserServiceRow {
    private Long userId;
    private String userName;
    private Long serviceId;
    private String serviceName;
    private Long quantity;
    private BigDecimal revenue;
    private Double appliedCommissionPercentage;
    private Boolean usedServiceCommission;
    private BigDecimal commissionAmount;
}
