package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SaleDailyRow {
    private LocalDate date;
    private Long salesCount;
    private BigDecimal revenue;
}
