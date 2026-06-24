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
@Table("appointments")
public class AppointmentEntity {
    @Id
    private Long id;
    @Column("client_id")
    private Long clientId;
    @Column("user_id")
    private Long userId;
    @Column("branch_id")
    private Long branchId;
    @Column("salon_id")
    private Long salonId;
    @Column("service_id")
    private Long serviceId;
    @Column("start_at")
    private LocalDateTime startAt;
    @Column("end_at")
    private LocalDateTime endAt;
    private String status;
    private String notes;
    @Column("cancelled_at")
    private LocalDateTime cancelledAt;
    @Column("cancellation_reason")
    private String cancellationReason;
    @Column("created_at")
    private LocalDateTime createdAt;
    @Column("updated_at")
    private LocalDateTime updatedAt;
}
