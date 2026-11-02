package com.danydandy.SalonSpa.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionEvent {
    private Long id;
    private Long salonId;
    private String eventType;
    private String detail;
    private Long userId;
    private LocalDateTime createdAt;
}
