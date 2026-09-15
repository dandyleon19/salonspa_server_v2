package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.domain.ports.out.SaleReportRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class SaleReportRepositoryAdapter implements SaleReportRepositoryPort {

    private final SaleReportRepository saleReportRepository;

    @Override
    public Mono<SaleSummaryRow> getSummary(Long salonId, LocalDateTime from, LocalDateTime to, Long branchId,
                                           Long userId) {
        return saleReportRepository.getSummary(salonId, from, to, branchId, userId);
    }

    @Override
    public Flux<SalePaymentMethodRow> getByPaymentMethod(Long salonId, LocalDateTime from, LocalDateTime to,
                                                         Long branchId, Long userId) {
        return saleReportRepository.getByPaymentMethod(salonId, from, to, branchId, userId);
    }

    @Override
    public Flux<SaleServiceRow> getByService(Long salonId, LocalDateTime from, LocalDateTime to, Long branchId,
                                             Long userId) {
        return saleReportRepository.getByService(salonId, from, to, branchId, userId);
    }

    @Override
    public Flux<SaleProfessionalRow> getByProfessional(Long salonId, LocalDateTime from, LocalDateTime to,
                                                       Long branchId, Long userId) {
        return saleReportRepository.getByProfessional(salonId, from, to, branchId, userId);
    }

    @Override
    public Flux<SaleDailyRow> getDailyBreakdown(Long salonId, LocalDateTime from, LocalDateTime to, Long branchId,
                                                Long userId) {
        return saleReportRepository.getDailyBreakdown(salonId, from, to, branchId, userId);
    }
}
