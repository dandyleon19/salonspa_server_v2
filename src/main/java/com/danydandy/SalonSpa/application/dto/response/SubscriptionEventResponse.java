package com.danydandy.SalonSpa.application.dto.response;

import com.danydandy.SalonSpa.domain.model.SubscriptionEvent;

import java.time.LocalDateTime;

public record SubscriptionEventResponse(
        Long id,
        String eventType,
        String detail,
        Long userId,
        LocalDateTime createdAt
) {
    public static SubscriptionEventResponse from(SubscriptionEvent event) {
        return new SubscriptionEventResponse(
                event.getId(),
                event.getEventType(),
                event.getDetail(),
                event.getUserId(),
                event.getCreatedAt()
        );
    }
}
