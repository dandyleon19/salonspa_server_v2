package com.danydandy.SalonSpa.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserServiceCommission {
    private Long id;
    private Long userId;
    private Long serviceId;
    private Double commissionPercentage;
    private String serviceName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
