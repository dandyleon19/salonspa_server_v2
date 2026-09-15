package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.SaleEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface SaleRepository extends R2dbcRepository<SaleEntity, Long> {

    @Query("""
            SELECT s.id, s.salon_id, s.branch_id, s.client_id, s.registered_by_user_id, s.appointment_id,
                   s.subtotal, s.discount_amount, s.total_amount, s.amount_paid, s.status, s.notes,
                   s.sold_at, s.cancelled_at, s.cancellation_reason, s.created_at, s.updated_at
            FROM sales s
            WHERE (:branchId IS NULL OR s.branch_id = :branchId)
              AND (:clientId IS NULL OR s.client_id = :clientId)
              AND (:status IS NULL OR s.status = :status)
              AND (:from IS NULL OR CAST(s.sold_at AS DATE) >= :from)
              AND (:to IS NULL OR CAST(s.sold_at AS DATE) <= :to)
            ORDER BY s.sold_at DESC, s.id DESC
            LIMIT :limit OFFSET :offset
            """)
    Flux<SaleEntity> findPage(Long branchId, Long clientId, String status, LocalDate from, LocalDate to,
                              int limit, long offset);

    @Query("""
            SELECT COUNT(*)
            FROM sales s
            WHERE (:branchId IS NULL OR s.branch_id = :branchId)
              AND (:clientId IS NULL OR s.client_id = :clientId)
              AND (:status IS NULL OR s.status = :status)
              AND (:from IS NULL OR CAST(s.sold_at AS DATE) >= :from)
              AND (:to IS NULL OR CAST(s.sold_at AS DATE) <= :to)
            """)
    Mono<Long> countFiltered(Long branchId, Long clientId, String status, LocalDate from, LocalDate to);

    @Query("""
            SELECT s.id, s.salon_id, s.branch_id, s.client_id, s.registered_by_user_id, s.appointment_id,
                   s.subtotal, s.discount_amount, s.total_amount, s.amount_paid, s.status, s.notes,
                   s.sold_at, s.cancelled_at, s.cancellation_reason, s.created_at, s.updated_at
            FROM sales s
            WHERE s.salon_id = :salonId
              AND (:branchId IS NULL OR s.branch_id = :branchId)
              AND (:clientId IS NULL OR s.client_id = :clientId)
              AND (:status IS NULL OR s.status = :status)
              AND (:from IS NULL OR CAST(s.sold_at AS DATE) >= :from)
              AND (:to IS NULL OR CAST(s.sold_at AS DATE) <= :to)
            ORDER BY s.sold_at DESC, s.id DESC
            LIMIT :limit OFFSET :offset
            """)
    Flux<SaleEntity> findPageBySalonId(Long salonId, Long branchId, Long clientId, String status, LocalDate from,
                                       LocalDate to, int limit, long offset);

    @Query("""
            SELECT COUNT(*)
            FROM sales s
            WHERE s.salon_id = :salonId
              AND (:branchId IS NULL OR s.branch_id = :branchId)
              AND (:clientId IS NULL OR s.client_id = :clientId)
              AND (:status IS NULL OR s.status = :status)
              AND (:from IS NULL OR CAST(s.sold_at AS DATE) >= :from)
              AND (:to IS NULL OR CAST(s.sold_at AS DATE) <= :to)
            """)
    Mono<Long> countBySalonId(Long salonId, Long branchId, Long clientId, String status, LocalDate from, LocalDate to);
}
