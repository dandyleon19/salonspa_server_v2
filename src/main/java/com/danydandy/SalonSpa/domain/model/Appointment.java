package com.danydandy.SalonSpa.domain.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {
    private Long id;
    private Long clientId;
    private Long userId;
    private Long branchId;
    private Long salonId;
    private Long serviceId;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private AppointmentStatus status;
    private String notes;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    private String clientName;
    private String clientPhone;
    private String clientEmail;
    private LocalDate clientBirthDate;
    private Gender clientGender;
    private String userName;
    private String branchName;
    private String serviceName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
