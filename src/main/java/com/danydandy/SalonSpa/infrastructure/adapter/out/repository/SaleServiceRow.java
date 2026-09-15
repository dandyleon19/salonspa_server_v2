package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaleServiceRow {
    private Long serviceId;
    private String serviceName;
    private Long quantity;
    private BigDecimal revenue;
}
