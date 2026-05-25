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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "clinical_record_services")
public class ClinicalRecordServiceEntity {
    @Id
    private Long id;
    private String notes;

    // Relations
    @Column("clinical_record_id")
    private Long clinicalRecordId;
    @Column("service_id")
    private Long serviceId;

    @Column("created_at")
    private LocalDateTime createdAt;
}
