package com.danydandy.SalonSpa.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClinicalRecord {
    private Long id;
    private String diagnosis;
    private String treatment;
    private String observations;
    private LocalDateTime sessionDate;
    private Long clientId;
    private Long userId;
    private Long branchId;
}
