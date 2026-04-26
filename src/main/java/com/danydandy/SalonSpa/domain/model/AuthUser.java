package com.danydandy.SalonSpa.domain.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthUser {
    private Long userId;
    private Long salonId;
}
