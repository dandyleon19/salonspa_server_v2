package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.infrastructure.adapter.out.entity.SalonEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface SalonRepository extends R2dbcRepository<SalonEntity, Long> {
}
