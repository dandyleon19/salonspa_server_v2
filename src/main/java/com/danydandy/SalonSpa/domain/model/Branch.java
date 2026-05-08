package com.danydandy.SalonSpa.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Branch {
    private Long id;
    private String name;
    private String address;
    private String city;
    private Long salonId;
    private Salon salon;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
