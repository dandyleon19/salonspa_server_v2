package com.danydandy.SalonSpa.domain.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Salon {
    private Long id;
    private String name;
    private String socialReason;
    private String fiscalAddress;
    private String rucNumber;
    private String phone;
    private List<Branch> branches;
}
