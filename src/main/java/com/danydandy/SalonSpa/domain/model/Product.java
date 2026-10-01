package com.danydandy.SalonSpa.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private Long id;
    private String name;
    private String description;
    private String longDescription;
    private BigDecimal price;
    private Integer stockQuantity;
    private Integer lowStockThreshold;
    private Boolean isActive;
    private String imageUrl;
    private Long salonId;
    private Long categoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
