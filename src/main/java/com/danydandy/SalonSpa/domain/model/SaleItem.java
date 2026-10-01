package com.danydandy.SalonSpa.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaleItem {
    private Long id;
    private Long saleId;
    private Long serviceId;
    private Long productId;
    private Long userId;
    private Long appointmentId;
    private String serviceName;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountAmount;
    private BigDecimal lineTotal;
    private String userName;
    private LocalDateTime createdAt;
}
