package com.danydandy.SalonSpa.infrastructure.adapter.in;

import com.danydandy.SalonSpa.application.dto.response.CommissionReportResponse;
import com.danydandy.SalonSpa.domain.ports.in.CommissionReportUseCase;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/commissions")
@RequiredArgsConstructor
@Validated
public class CommissionController {

    private final CommissionReportUseCase commissionReportUseCase;

    @GetMapping("/report")
    public Mono<ResponseEntity<CommissionReportResponse>> getReport(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) @Positive Long branchId,
            @RequestParam(required = false) @Positive Long userId
    ) {
        return commissionReportUseCase.getReport(from, to, branchId, userId)
                .map(ResponseEntity::ok);
    }
}
