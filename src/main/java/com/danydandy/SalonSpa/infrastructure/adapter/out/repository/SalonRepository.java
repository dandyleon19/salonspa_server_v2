package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.SalonEntity;
import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.SalonLastSessionRow;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

public interface SalonRepository extends R2dbcRepository<SalonEntity, Long> {

    /** For each salon, its most recent session and whose it was. The platform admin's own sessions do not count. */
    @Query("""
            SELECT DISTINCT ON (salon_id)
                   salon_id, last_login_at, TRIM(CONCAT(first_name, ' ', last_name)) AS user_name
            FROM users
            WHERE salon_id IN (:salonIds)
              AND role <> 'SUPER_ADMIN'
              AND last_login_at IS NOT NULL
            ORDER BY salon_id, last_login_at DESC
            """)
    Flux<SalonLastSessionRow> findLastSessions(Collection<Long> salonIds);
    Flux<SalonEntity> findAllByOrderByCreatedAtAscIdAsc(Pageable pageable);

    @Query("""
            SELECT *
            FROM salons
            WHERE (
                :search IS NULL
                OR name ILIKE :search
                OR social_reason ILIKE :search
                OR ruc_number ILIKE :search
            )
            ORDER BY created_at ASC, id ASC
            LIMIT :limit OFFSET :offset
            """)
    Flux<SalonEntity> findPage(String search, int limit, long offset);

    @Query("""
            SELECT COUNT(*)
            FROM salons
            WHERE (
                :search IS NULL
                OR name ILIKE :search
                OR social_reason ILIKE :search
                OR ruc_number ILIKE :search
            )
            """)
    Mono<Long> countFiltered(String search);
}
