package com.danydandy.SalonSpa.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Service {
    private Long id;
    private String name;
    private String description;
    private String longDescription;
    private BigDecimal price;
    private Integer durationMinutes;
    private Boolean isActive;
    private Long salonId;
    private Long categoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
