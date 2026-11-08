package com.danydandy.SalonSpa.domain.model;

import lombok.*;

import java.time.LocalDateTime;
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
    private String logoUrl;
    private List<Branch> branches;
    /** Only filled for the platform admin: the most recent session of any user of the salon, and whose it was. */
    private LocalDateTime lastSessionAt;
    private String lastSessionBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
