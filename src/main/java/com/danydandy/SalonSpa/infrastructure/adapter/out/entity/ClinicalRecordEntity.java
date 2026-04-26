package com.danydandy.SalonSpa.infrastructure.adapter.out.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("clinical_records")
public class ClinicalRecordEntity {
    @Id
    private Long id;
    private String diagnosis;
    private String treatment;
    private String observations;
    @Column("session_date")
    private LocalDateTime sessionDate;
    @Column("client_id")
    private Long clientId;
    @Column("user_id")
    private Long userId;
    @Column("branch_id")
    private Long branchId;
}
