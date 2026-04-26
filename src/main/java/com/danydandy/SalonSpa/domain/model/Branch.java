package com.danydandy.SalonSpa.domain.model;

import lombok.*;

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
}
