package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaleProfessionalRow {
    private Long userId;
    private Long itemsSold;
    private BigDecimal revenue;
}
