package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.SaleEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface SaleReportRepository extends Repository<SaleEntity, Long> {

    @Query("""
            SELECT COUNT(DISTINCT s.id) AS total_sales,
                   COALESCE(SUM(
                       CASE WHEN :userId IS NULL THEN s.total_amount
                            ELSE (
                                SELECT COALESCE(SUM(si.line_total), 0)
                                FROM sale_items si
                                WHERE si.sale_id = s.id AND si.user_id = :userId
                            )
                       END
                   ), 0) AS total_revenue,
                   COALESCE(SUM(s.amount_paid), 0) AS total_paid,
                   COALESCE(SUM(
                       CASE WHEN s.status = 'PARTIALLY_PAID' THEN s.total_amount - s.amount_paid ELSE 0 END
                   ), 0) AS total_outstanding
            FROM sales s
            WHERE s.status != 'CANCELLED'
              AND s.sold_at >= :from
              AND s.sold_at < :to
              AND (:salonId IS NULL OR s.salon_id = :salonId)
              AND (:branchId IS NULL OR s.branch_id = :branchId)
              AND (:userId IS NULL OR EXISTS (
                  SELECT 1 FROM sale_items si WHERE si.sale_id = s.id AND si.user_id = :userId
              ))
            """)
    Mono<SaleSummaryRow> getSummary(Long salonId, LocalDateTime from, LocalDateTime to, Long branchId, Long userId);

    @Query("""
            SELECT sp.payment_method, COALESCE(SUM(sp.amount), 0) AS total_amount
            FROM sale_payments sp
            INNER JOIN sales s ON s.id = sp.sale_id
            WHERE s.status != 'CANCELLED'
              AND s.sold_at >= :from
              AND s.sold_at < :to
              AND (:salonId IS NULL OR s.salon_id = :salonId)
              AND (:branchId IS NULL OR s.branch_id = :branchId)
              AND (:userId IS NULL OR EXISTS (
                  SELECT 1 FROM sale_items si WHERE si.sale_id = s.id AND si.user_id = :userId
              ))
            GROUP BY sp.payment_method
            ORDER BY total_amount DESC
            """)
    Flux<SalePaymentMethodRow> getByPaymentMethod(Long salonId, LocalDateTime from, LocalDateTime to, Long branchId,
                                                  Long userId);

    @Query("""
            SELECT si.service_id, si.service_name,
                   COALESCE(SUM(si.quantity), 0) AS quantity,
                   COALESCE(SUM(si.line_total), 0) AS revenue
            FROM sale_items si
            INNER JOIN sales s ON s.id = si.sale_id
            WHERE s.status != 'CANCELLED'
              AND s.sold_at >= :from
              AND s.sold_at < :to
              AND (:salonId IS NULL OR s.salon_id = :salonId)
              AND (:branchId IS NULL OR s.branch_id = :branchId)
              AND (:userId IS NULL OR si.user_id = :userId)
            GROUP BY si.service_id, si.service_name
            ORDER BY revenue DESC
            """)
    Flux<SaleServiceRow> getByService(Long salonId, LocalDateTime from, LocalDateTime to, Long branchId, Long userId);

    @Query("""
            SELECT si.user_id,
                   COALESCE(SUM(si.quantity), 0) AS items_sold,
                   COALESCE(SUM(si.line_total), 0) AS revenue
            FROM sale_items si
            INNER JOIN sales s ON s.id = si.sale_id
            WHERE s.status != 'CANCELLED'
              AND s.sold_at >= :from
              AND s.sold_at < :to
              AND (:salonId IS NULL OR s.salon_id = :salonId)
              AND (:branchId IS NULL OR s.branch_id = :branchId)
              AND (:userId IS NULL OR si.user_id = :userId)
            GROUP BY si.user_id
            ORDER BY revenue DESC
            """)
    Flux<SaleProfessionalRow> getByProfessional(Long salonId, LocalDateTime from, LocalDateTime to, Long branchId,
                                                Long userId);

    @Query("""
            SELECT CAST(s.sold_at AS DATE) AS date,
                   COUNT(DISTINCT s.id) AS sales_count,
                   COALESCE(SUM(
                       CASE WHEN :userId IS NULL THEN s.total_amount
                            ELSE (
                                SELECT COALESCE(SUM(si.line_total), 0)
                                FROM sale_items si
                                WHERE si.sale_id = s.id AND si.user_id = :userId
                            )
                       END
                   ), 0) AS revenue
            FROM sales s
            WHERE s.status != 'CANCELLED'
              AND s.sold_at >= :from
              AND s.sold_at < :to
              AND (:salonId IS NULL OR s.salon_id = :salonId)
              AND (:branchId IS NULL OR s.branch_id = :branchId)
              AND (:userId IS NULL OR EXISTS (
                  SELECT 1 FROM sale_items si WHERE si.sale_id = s.id AND si.user_id = :userId
              ))
            GROUP BY CAST(s.sold_at AS DATE)
            ORDER BY date ASC
            """)
    Flux<SaleDailyRow> getDailyBreakdown(Long salonId, LocalDateTime from, LocalDateTime to, Long branchId,
                                         Long userId);
}
