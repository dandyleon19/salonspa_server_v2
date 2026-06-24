package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.AppointmentEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface DashboardRepository extends Repository<AppointmentEntity, Long> {

    @Query("""
            SELECT COUNT(*)
            FROM clients
            WHERE (:salonId IS NULL OR salon_id = :salonId)
            """)
    Mono<Long> countClients(Long salonId);

    @Query("""
            SELECT COUNT(*)
            FROM users
            WHERE is_active = true
              AND (:salonId IS NULL OR salon_id = :salonId)
            """)
    Mono<Long> countActiveUsers(Long salonId);

    @Query("""
            SELECT COUNT(*)
            FROM appointments
            WHERE CAST(start_at AS DATE) = :date
              AND (:salonId IS NULL OR salon_id = :salonId)
              AND (:branchId IS NULL OR branch_id = :branchId)
              AND (:userId IS NULL OR user_id = :userId)
            """)
    Mono<Long> countAppointmentsOnDate(Long salonId, LocalDate date, Long branchId, Long userId);

    @Query("""
            SELECT COUNT(*)
            FROM appointments
            WHERE CAST(start_at AS DATE) = :date
              AND status = :status
              AND (:salonId IS NULL OR salon_id = :salonId)
              AND (:branchId IS NULL OR branch_id = :branchId)
              AND (:userId IS NULL OR user_id = :userId)
            """)
    Mono<Long> countAppointmentsOnDateByStatus(Long salonId, LocalDate date, String status, Long branchId,
                                               Long userId);

    @Query("""
            SELECT status, COUNT(*) AS count
            FROM appointments
            WHERE start_at >= :monthStart
              AND start_at < :monthEnd
              AND (:salonId IS NULL OR salon_id = :salonId)
              AND (:branchId IS NULL OR branch_id = :branchId)
              AND (:userId IS NULL OR user_id = :userId)
            GROUP BY status
            """)
    Flux<DashboardStatusCountRow> countAppointmentsByStatusInMonth(Long salonId, LocalDateTime monthStart,
                                                                   LocalDateTime monthEnd, Long branchId,
                                                                   Long userId);
}
