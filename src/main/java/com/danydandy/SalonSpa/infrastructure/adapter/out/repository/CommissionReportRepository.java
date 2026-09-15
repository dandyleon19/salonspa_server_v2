package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.SaleEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface CommissionReportRepository extends Repository<SaleEntity, Long> {

    @Query("""
            SELECT COALESCE(SUM(si.quantity), 0) AS total_items,
                   COALESCE(SUM(si.line_total), 0) AS total_revenue,
                   COALESCE(SUM(
                       si.line_total * COALESCE(usc.commission_percentage, u.commission_percentage, 0) / 100.0
                   ), 0) AS total_commission
            FROM sale_items si
            INNER JOIN sales s ON s.id = si.sale_id
            INNER JOIN users u ON u.id = si.user_id
            LEFT JOIN user_service_commissions usc
                ON usc.user_id = si.user_id AND usc.service_id = si.service_id
            WHERE s.status != 'CANCELLED'
              AND s.sold_at >= :from
              AND s.sold_at < :to
              AND (:salonId IS NULL OR s.salon_id = :salonId)
              AND (:branchId IS NULL OR s.branch_id = :branchId)
              AND (:userId IS NULL OR si.user_id = :userId)
            """)
    Mono<CommissionSummaryRow> getSummary(Long salonId, LocalDateTime from, LocalDateTime to, Long branchId,
                                          Long userId);

    @Query("""
            SELECT si.user_id,
                   TRIM(CONCAT(u.first_name, ' ', u.last_name)) AS user_name,
                   u.commission_percentage AS default_commission_percentage,
                   COALESCE(SUM(si.quantity), 0) AS items_sold,
                   COALESCE(SUM(si.line_total), 0) AS revenue,
                   COALESCE(SUM(
                       si.line_total * COALESCE(usc.commission_percentage, u.commission_percentage, 0) / 100.0
                   ), 0) AS commission_amount
            FROM sale_items si
            INNER JOIN sales s ON s.id = si.sale_id
            INNER JOIN users u ON u.id = si.user_id
            LEFT JOIN user_service_commissions usc
                ON usc.user_id = si.user_id AND usc.service_id = si.service_id
            WHERE s.status != 'CANCELLED'
              AND s.sold_at >= :from
              AND s.sold_at < :to
              AND (:salonId IS NULL OR s.salon_id = :salonId)
              AND (:branchId IS NULL OR s.branch_id = :branchId)
              AND (:userId IS NULL OR si.user_id = :userId)
            GROUP BY si.user_id, u.first_name, u.last_name, u.commission_percentage
            ORDER BY commission_amount DESC
            """)
    Flux<CommissionUserRow> getByUser(Long salonId, LocalDateTime from, LocalDateTime to, Long branchId, Long userId);

    @Query("""
            SELECT si.user_id,
                   TRIM(CONCAT(u.first_name, ' ', u.last_name)) AS user_name,
                   si.service_id,
                   si.service_name,
                   COALESCE(SUM(si.quantity), 0) AS quantity,
                   COALESCE(SUM(si.line_total), 0) AS revenue,
                   COALESCE(usc.commission_percentage, u.commission_percentage, 0) AS applied_commission_percentage,
                   (usc.id IS NOT NULL) AS used_service_commission,
                   COALESCE(SUM(
                       si.line_total * COALESCE(usc.commission_percentage, u.commission_percentage, 0) / 100.0
                   ), 0) AS commission_amount
            FROM sale_items si
            INNER JOIN sales s ON s.id = si.sale_id
            INNER JOIN users u ON u.id = si.user_id
            LEFT JOIN user_service_commissions usc
                ON usc.user_id = si.user_id AND usc.service_id = si.service_id
            WHERE s.status != 'CANCELLED'
              AND s.sold_at >= :from
              AND s.sold_at < :to
              AND (:salonId IS NULL OR s.salon_id = :salonId)
              AND (:branchId IS NULL OR s.branch_id = :branchId)
              AND (:userId IS NULL OR si.user_id = :userId)
            GROUP BY si.user_id, u.first_name, u.last_name, u.commission_percentage,
                     si.service_id, si.service_name, usc.id, usc.commission_percentage
            ORDER BY user_name ASC, commission_amount DESC
            """)
    Flux<CommissionUserServiceRow> getByUserAndService(Long salonId, LocalDateTime from, LocalDateTime to,
                                                       Long branchId, Long userId);
}
