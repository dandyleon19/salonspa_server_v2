package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.domain.ports.out.CommissionReportRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class CommissionReportRepositoryAdapter implements CommissionReportRepositoryPort {

    private final CommissionReportRepository commissionReportRepository;

    @Override
    public Mono<CommissionSummaryRow> getSummary(Long salonId, LocalDateTime from, LocalDateTime to, Long branchId,
                                                 Long userId) {
        return commissionReportRepository.getSummary(salonId, from, to, branchId, userId);
    }

    @Override
    public Flux<CommissionUserRow> getByUser(Long salonId, LocalDateTime from, LocalDateTime to, Long branchId,
                                             Long userId) {
        return commissionReportRepository.getByUser(salonId, from, to, branchId, userId);
    }

    @Override
    public Flux<CommissionUserServiceRow> getByUserAndService(Long salonId, LocalDateTime from, LocalDateTime to,
                                                              Long branchId, Long userId) {
        return commissionReportRepository.getByUserAndService(salonId, from, to, branchId, userId);
    }
}
