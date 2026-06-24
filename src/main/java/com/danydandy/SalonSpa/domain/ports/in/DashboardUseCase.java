package com.danydandy.SalonSpa.domain.ports.in;

import com.danydandy.SalonSpa.application.dto.response.DashboardResponse;

import java.time.LocalDate;

import reactor.core.publisher.Mono;

public interface DashboardUseCase {
    Mono<DashboardResponse> getSummary(LocalDate date, Long branchId, Long userId);
}
