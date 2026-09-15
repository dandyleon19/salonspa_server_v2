package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CommissionUserRow {
    private Long userId;
    private String userName;
    private Double defaultCommissionPercentage;
    private Long itemsSold;
    private BigDecimal revenue;
    private BigDecimal commissionAmount;
}
