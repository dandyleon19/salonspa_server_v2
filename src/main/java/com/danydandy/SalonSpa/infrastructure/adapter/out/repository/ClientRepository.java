package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.ClientEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClientRepository extends R2dbcRepository<ClientEntity, Long> {

    @Query("""
            SELECT id, first_name, last_name, document_number, phone, email, birth_date, gender,
                   salon_id, created_at, updated_at
            FROM clients
            WHERE (
                :search IS NULL
                OR first_name ILIKE :search
                OR last_name ILIKE :search
                OR document_number ILIKE :search
                OR phone ILIKE :search
                OR email ILIKE :search
            )
            ORDER BY created_at ASC
            LIMIT :limit OFFSET :offset
            """)
    Flux<ClientEntity> findPage(String search, int limit, long offset);

    @Query("""
            SELECT COUNT(*)
            FROM clients
            WHERE (
                :search IS NULL
                OR first_name ILIKE :search
                OR last_name ILIKE :search
                OR document_number ILIKE :search
                OR phone ILIKE :search
                OR email ILIKE :search
            )
            """)
    Mono<Long> countFiltered(String search);

    @Query("""
            SELECT id, first_name, last_name, document_number, phone, email, birth_date, gender,
                   salon_id, created_at, updated_at
            FROM clients
            WHERE salon_id = :salonId
              AND (
                  :search IS NULL
                  OR first_name ILIKE :search
                  OR last_name ILIKE :search
                  OR document_number ILIKE :search
                  OR phone ILIKE :search
                  OR email ILIKE :search
              )
            ORDER BY created_at ASC
            LIMIT :limit OFFSET :offset
            """)
    Flux<ClientEntity> findPageBySalonId(Long salonId, String search, int limit, long offset);

    @Query("""
            SELECT COUNT(*)
            FROM clients
            WHERE salon_id = :salonId
              AND (
                  :search IS NULL
                  OR first_name ILIKE :search
                  OR last_name ILIKE :search
                  OR document_number ILIKE :search
                  OR phone ILIKE :search
                  OR email ILIKE :search
              )
            """)
    Mono<Long> countBySalonId(Long salonId, String search);
}
