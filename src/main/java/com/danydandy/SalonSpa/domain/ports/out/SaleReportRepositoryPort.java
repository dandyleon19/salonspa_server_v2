package com.danydandy.SalonSpa.domain.ports.out;

import com.danydandy.SalonSpa.infrastructure.adapter.out.repository.SaleDailyRow;
import com.danydandy.SalonSpa.infrastructure.adapter.out.repository.SalePaymentMethodRow;
import com.danydandy.SalonSpa.infrastructure.adapter.out.repository.SaleProfessionalRow;
import com.danydandy.SalonSpa.infrastructure.adapter.out.repository.SaleServiceRow;
import com.danydandy.SalonSpa.infrastructure.adapter.out.repository.SaleSummaryRow;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface SaleReportRepositoryPort {
    Mono<SaleSummaryRow> getSummary(Long salonId, LocalDateTime from, LocalDateTime to, Long branchId, Long userId);

    Flux<SalePaymentMethodRow> getByPaymentMethod(Long salonId, LocalDateTime from, LocalDateTime to, Long branchId,
                                                  Long userId);

    Flux<SaleServiceRow> getByService(Long salonId, LocalDateTime from, LocalDateTime to, Long branchId, Long userId);

    Flux<SaleProfessionalRow> getByProfessional(Long salonId, LocalDateTime from, LocalDateTime to, Long branchId,
                                                Long userId);

    Flux<SaleDailyRow> getDailyBreakdown(Long salonId, LocalDateTime from, LocalDateTime to, Long branchId,
                                         Long userId);
}
