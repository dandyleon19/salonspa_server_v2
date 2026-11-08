package com.danydandy.SalonSpa.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Boolean isActive;
    private Double commissionPercentage;
    private Role role;
    private Long salonId;
    private Salon salon;
    /** When the user last signed in or renewed their session (null = never). */
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
