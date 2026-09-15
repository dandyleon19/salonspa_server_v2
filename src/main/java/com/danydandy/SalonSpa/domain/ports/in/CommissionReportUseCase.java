package com.danydandy.SalonSpa.domain.ports.in;

import com.danydandy.SalonSpa.application.dto.response.CommissionReportResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface CommissionReportUseCase {
    Mono<CommissionReportResponse> getReport(LocalDate from, LocalDate to, Long branchId, Long userId);
}
