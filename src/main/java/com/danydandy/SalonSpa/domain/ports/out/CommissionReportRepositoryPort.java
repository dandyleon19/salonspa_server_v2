package com.danydandy.SalonSpa.domain.ports.out;

import com.danydandy.SalonSpa.infrastructure.adapter.out.repository.CommissionSummaryRow;
import com.danydandy.SalonSpa.infrastructure.adapter.out.repository.CommissionUserRow;
import com.danydandy.SalonSpa.infrastructure.adapter.out.repository.CommissionUserServiceRow;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface CommissionReportRepositoryPort {
    Mono<CommissionSummaryRow> getSummary(Long salonId, LocalDateTime from, LocalDateTime to, Long branchId,
                                          Long userId);

    Flux<CommissionUserRow> getByUser(Long salonId, LocalDateTime from, LocalDateTime to, Long branchId, Long userId);

    Flux<CommissionUserServiceRow> getByUserAndService(Long salonId, LocalDateTime from, LocalDateTime to,
                                                       Long branchId, Long userId);
}
