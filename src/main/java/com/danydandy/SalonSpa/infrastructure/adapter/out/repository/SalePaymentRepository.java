package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.SalePaymentEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface SalePaymentRepository extends R2dbcRepository<SalePaymentEntity, Long> {

    @Query("""
            SELECT id, sale_id, amount, payment_method, reference, paid_at, created_at
            FROM sale_payments
            WHERE sale_id = :saleId
            ORDER BY paid_at ASC, id ASC
            """)
    Flux<SalePaymentEntity> findBySaleId(Long saleId);
}
