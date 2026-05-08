package com.danydandy.SalonSpa.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceCategory {
    private Long id;
    private String name;
    private String description;
    private String longDescription;
    private Long salonId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
