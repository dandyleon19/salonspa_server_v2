package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.SaleItemEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface SaleItemRepository extends R2dbcRepository<SaleItemEntity, Long> {

    @Query("""
            SELECT id, sale_id, service_id, user_id, appointment_id, service_name, quantity,
                   unit_price, discount_amount, line_total, created_at
            FROM sale_items
            WHERE sale_id = :saleId
            ORDER BY id ASC
            """)
    Flux<SaleItemEntity> findBySaleId(Long saleId);
}
