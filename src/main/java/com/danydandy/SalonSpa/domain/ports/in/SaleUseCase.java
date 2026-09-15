package com.danydandy.SalonSpa.domain.ports.in;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.application.dto.response.SaleReportResponse;
import com.danydandy.SalonSpa.domain.model.Sale;
import com.danydandy.SalonSpa.domain.model.SaleItem;
import com.danydandy.SalonSpa.domain.model.SalePayment;
import com.danydandy.SalonSpa.domain.model.SaleStatus;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

public interface SaleUseCase {
    Mono<Sale> create(Sale sale, List<SaleItem> items, List<SalePayment> payments);

    Mono<PageResponse<Sale>> findPage(int page, int size, Long branchId, Long clientId, SaleStatus status,
                                      LocalDate from, LocalDate to);

    Mono<Sale> findById(Long id);

    Mono<Sale> addPayment(Long saleId, SalePayment payment);

    Mono<Sale> cancel(Long id, String reason);

    Mono<SaleReportResponse> getReport(LocalDate from, LocalDate to, Long branchId, Long userId);
}
