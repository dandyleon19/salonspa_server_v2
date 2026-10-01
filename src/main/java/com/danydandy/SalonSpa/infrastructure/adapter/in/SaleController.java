package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.application.dto.request.AddSalePaymentRequest;
import com.danydandy.SalonSpa.application.dto.request.CancelSaleRequest;
import com.danydandy.SalonSpa.application.dto.request.CreateSaleRequest;
import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.application.dto.response.SaleReportResponse;
import com.danydandy.SalonSpa.application.dto.response.SaleResponse;
import com.danydandy.SalonSpa.application.mapper.RequestDtoMapper;
import com.danydandy.SalonSpa.domain.model.SaleItem;
import com.danydandy.SalonSpa.domain.model.SalePayment;
import com.danydandy.SalonSpa.domain.model.SaleStatus;
import com.danydandy.SalonSpa.domain.ports.in.SaleUseCase;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.SaleMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@Validated
public class SaleController {

    private final SaleUseCase saleUseCase;
    private final RequestDtoMapper requestDtoMapper;
    private final SaleMapper saleMapper;

    @PostMapping
    public Mono<ResponseEntity<SaleResponse>> create(@Valid @RequestBody CreateSaleRequest request) {
        List<SaleItem> items = request.getItems().stream().map(requestDtoMapper::toSaleItem).toList();
        List<SalePayment> payments = request.getPayments() == null
                ? List.of()
                : request.getPayments().stream().map(requestDtoMapper::toSalePayment).toList();
        return saleUseCase.create(requestDtoMapper.toSale(request), items, payments)
                .map(saleMapper::toResponse)
                .map(sale -> ResponseEntity.status(HttpStatus.CREATED).body(sale));
    }

    @GetMapping
    public Mono<ResponseEntity<PageResponse<SaleResponse>>> getAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Positive @Max(100) int size,
            @RequestParam(required = false) @Positive Long branchId,
            @RequestParam(required = false) @Positive Long clientId,
            @RequestParam(required = false) SaleStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return saleUseCase.findPage(page, size, branchId, clientId, status, from, to)
                .map(pageResponse -> PageResponse.of(
                        pageResponse.content().stream().map(saleMapper::toResponse).toList(),
                        pageResponse.page(),
                        pageResponse.size(),
                        pageResponse.totalElements()
                ))
                .map(ResponseEntity::ok);
    }

    @GetMapping("/report")
    public Mono<ResponseEntity<SaleReportResponse>> getReport(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) @Positive Long branchId,
            @RequestParam(required = false) @Positive Long userId
    ) {
        return saleUseCase.getReport(from, to, branchId, userId)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<SaleResponse>> getById(@PathVariable @Positive Long id) {
        return saleUseCase.findById(id)
                .map(saleMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/{id}/payments")
    public Mono<ResponseEntity<SaleResponse>> addPayment(
            @PathVariable @Positive Long id,
            @Valid @RequestBody AddSalePaymentRequest request
    ) {
        return saleUseCase.addPayment(id, requestDtoMapper.toSalePayment(request))
                .map(saleMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}/cancel")
    public Mono<ResponseEntity<SaleResponse>> cancel(
            @PathVariable @Positive Long id,
            @Valid @RequestBody(required = false) CancelSaleRequest request
    ) {
        String reason = request != null ? request.getReason() : null;
        return saleUseCase.cancel(id, reason)
                .map(saleMapper::toResponse)
                .map(ResponseEntity::ok);
    }
}
