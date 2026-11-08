package com.danydandy.SalonSpa.domain.model;

import java.time.LocalDateTime;

/** The most recent session of a salon: when, and who. */
public record SalonLastSession(Long salonId, LocalDateTime at, String userName) {
}
