package com.danydandy.SalonSpa.domain.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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
    private Long serviceId;
    private Long branchId;
    private String userName;
    private String branchName;
    private List<String> associatedServices;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
