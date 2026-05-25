package com.danydandy.SalonSpa.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClinicalRecordService {
    private Long id;
    private Long clinicalRecordId;
    private Long serviceId;
    private String notes;
    private LocalDateTime createdAt;
}
