package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.AppointmentEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface AppointmentRepository extends R2dbcRepository<AppointmentEntity, Long> {

    @Query("""
            SELECT COUNT(*)
            FROM appointments
            WHERE user_id = :userId
              AND status NOT IN ('CANCELLED', 'NO_SHOW')
              AND start_at < :endAt
              AND end_at > :startAt
              AND (:excludeId IS NULL OR id != :excludeId)
            """)
    Mono<Long> countOverlapping(Long userId, LocalDateTime startAt, LocalDateTime endAt, Long excludeId);

    @Query("""
            SELECT a.id, a.client_id, a.user_id, a.branch_id, a.salon_id, a.service_id,
                   a.start_at, a.end_at, a.status, a.notes, a.cancelled_at, a.cancellation_reason,
                   a.created_at, a.updated_at
            FROM appointments a
            WHERE (:branchId IS NULL OR a.branch_id = :branchId)
              AND (:userId IS NULL OR a.user_id = :userId)
              AND (:clientId IS NULL OR a.client_id = :clientId)
              AND (:date IS NULL OR CAST(a.start_at AS DATE) = :date)
              AND (:status IS NULL OR a.status = :status)
            ORDER BY a.start_at ASC
            LIMIT :limit OFFSET :offset
            """)
    Flux<AppointmentEntity> findPage(Long branchId, Long userId, Long clientId, LocalDate date, String status,
                                   int limit, long offset);

    @Query("""
            SELECT COUNT(*)
            FROM appointments a
            WHERE (:branchId IS NULL OR a.branch_id = :branchId)
              AND (:userId IS NULL OR a.user_id = :userId)
              AND (:clientId IS NULL OR a.client_id = :clientId)
              AND (:date IS NULL OR CAST(a.start_at AS DATE) = :date)
              AND (:status IS NULL OR a.status = :status)
            """)
    Mono<Long> countFiltered(Long branchId, Long userId, Long clientId, LocalDate date, String status);

    @Query("""
            SELECT a.id, a.client_id, a.user_id, a.branch_id, a.salon_id, a.service_id,
                   a.start_at, a.end_at, a.status, a.notes, a.cancelled_at, a.cancellation_reason,
                   a.created_at, a.updated_at
            FROM appointments a
            WHERE a.salon_id = :salonId
              AND (:branchId IS NULL OR a.branch_id = :branchId)
              AND (:userId IS NULL OR a.user_id = :userId)
              AND (:clientId IS NULL OR a.client_id = :clientId)
              AND (:date IS NULL OR CAST(a.start_at AS DATE) = :date)
              AND (:status IS NULL OR a.status = :status)
            ORDER BY a.start_at ASC
            LIMIT :limit OFFSET :offset
            """)
    Flux<AppointmentEntity> findPageBySalonId(Long salonId, Long branchId, Long userId, Long clientId,
                                              LocalDate date, String status, int limit, long offset);

    @Query("""
            SELECT COUNT(*)
            FROM appointments a
            WHERE a.salon_id = :salonId
              AND (:branchId IS NULL OR a.branch_id = :branchId)
              AND (:userId IS NULL OR a.user_id = :userId)
              AND (:clientId IS NULL OR a.client_id = :clientId)
              AND (:date IS NULL OR CAST(a.start_at AS DATE) = :date)
              AND (:status IS NULL OR a.status = :status)
            """)
    Mono<Long> countBySalonId(Long salonId, Long branchId, Long userId, Long clientId, LocalDate date, String status);

    @Query("""
            SELECT a.id, a.client_id, a.user_id, a.branch_id, a.salon_id, a.service_id,
                   a.start_at, a.end_at, a.status, a.notes, a.cancelled_at, a.cancellation_reason,
                   a.created_at, a.updated_at
            FROM appointments a
            WHERE a.start_at >= :periodStart
              AND a.start_at < :periodEnd
              AND (:branchId IS NULL OR a.branch_id = :branchId)
              AND (:userId IS NULL OR a.user_id = :userId)
              AND (:status IS NULL OR a.status = :status)
            ORDER BY a.start_at ASC
            LIMIT :limit
            """)
    Flux<AppointmentEntity> findByPeriod(Long branchId, Long userId, String status, LocalDateTime periodStart,
                                         LocalDateTime periodEnd, int limit);

    @Query("""
            SELECT COUNT(*)
            FROM appointments a
            WHERE a.start_at >= :periodStart
              AND a.start_at < :periodEnd
              AND (:branchId IS NULL OR a.branch_id = :branchId)
              AND (:userId IS NULL OR a.user_id = :userId)
              AND (:status IS NULL OR a.status = :status)
            """)
    Mono<Long> countByPeriod(Long branchId, Long userId, String status, LocalDateTime periodStart,
                             LocalDateTime periodEnd);

    @Query("""
            SELECT a.id, a.client_id, a.user_id, a.branch_id, a.salon_id, a.service_id,
                   a.start_at, a.end_at, a.status, a.notes, a.cancelled_at, a.cancellation_reason,
                   a.created_at, a.updated_at
            FROM appointments a
            WHERE a.salon_id = :salonId
              AND a.start_at >= :periodStart
              AND a.start_at < :periodEnd
              AND (:branchId IS NULL OR a.branch_id = :branchId)
              AND (:userId IS NULL OR a.user_id = :userId)
              AND (:status IS NULL OR a.status = :status)
            ORDER BY a.start_at ASC
            LIMIT :limit
            """)
    Flux<AppointmentEntity> findBySalonIdAndPeriod(Long salonId, Long branchId, Long userId, String status,
                                                   LocalDateTime periodStart, LocalDateTime periodEnd, int limit);

    @Query("""
            SELECT COUNT(*)
            FROM appointments a
            WHERE a.salon_id = :salonId
              AND a.start_at >= :periodStart
              AND a.start_at < :periodEnd
              AND (:branchId IS NULL OR a.branch_id = :branchId)
              AND (:userId IS NULL OR a.user_id = :userId)
              AND (:status IS NULL OR a.status = :status)
            """)
    Mono<Long> countBySalonIdAndPeriod(Long salonId, Long branchId, Long userId, String status,
                                       LocalDateTime periodStart, LocalDateTime periodEnd);

    @Query("""
            SELECT a.id, a.client_id, a.user_id, a.branch_id, a.salon_id, a.service_id,
                   a.start_at, a.end_at, a.status, a.notes, a.cancelled_at, a.cancellation_reason,
                   a.created_at, a.updated_at
            FROM appointments a
            WHERE a.start_at >= :from
              AND a.status NOT IN ('CANCELLED', 'NO_SHOW')
              AND (:salonId IS NULL OR a.salon_id = :salonId)
              AND (:branchId IS NULL OR a.branch_id = :branchId)
              AND (:userId IS NULL OR a.user_id = :userId)
            ORDER BY a.start_at ASC
            LIMIT :limit
            """)
    Flux<AppointmentEntity> findUpcoming(Long salonId, Long branchId, Long userId, LocalDateTime from, int limit);
}
