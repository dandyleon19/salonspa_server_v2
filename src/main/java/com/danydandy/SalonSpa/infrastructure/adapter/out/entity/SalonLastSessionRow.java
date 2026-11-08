package com.danydandy.SalonSpa.infrastructure.adapter.out.entity;

import java.time.LocalDateTime;

/** A row of the "last session per salon" query (not a table). */
public record SalonLastSessionRow(Long salonId, LocalDateTime lastLoginAt, String userName) {
}
