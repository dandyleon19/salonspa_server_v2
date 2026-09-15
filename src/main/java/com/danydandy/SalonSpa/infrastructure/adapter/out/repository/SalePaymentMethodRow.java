package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalePaymentMethodRow {
    private String paymentMethod;
    private BigDecimal totalAmount;
}
