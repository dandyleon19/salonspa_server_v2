package com.danydandy.SalonSpa.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalePayment {
    private Long id;
    private Long saleId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String reference;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
}
