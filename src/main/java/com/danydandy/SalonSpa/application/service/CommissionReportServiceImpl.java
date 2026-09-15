package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.application.dto.response.CommissionReportResponse;
import com.danydandy.SalonSpa.application.dto.response.CommissionReportUserItem;
import com.danydandy.SalonSpa.application.dto.response.CommissionReportUserServiceItem;
import com.danydandy.SalonSpa.application.security.SecurityHelper;
import com.danydandy.SalonSpa.domain.exception.BadRequestException;
import com.danydandy.SalonSpa.domain.ports.in.CommissionReportUseCase;
import com.danydandy.SalonSpa.domain.ports.out.CommissionReportRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.adapter.out.repository.CommissionSummaryRow;
import com.danydandy.SalonSpa.infrastructure.adapter.out.repository.CommissionUserRow;
import com.danydandy.SalonSpa.infrastructure.adapter.out.repository.CommissionUserServiceRow;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class CommissionReportServiceImpl implements CommissionReportUseCase {

    private static final int MONEY_SCALE = 2;

    private final CommissionReportRepositoryPort commissionReportRepositoryPort;

    @Override
    public Mono<CommissionReportResponse> getReport(LocalDate from, LocalDate to, Long branchId, Long userId) {
        if (from == null || to == null) {
            return Mono.error(new BadRequestException("From and to dates are required"));
        }
        if (from.isAfter(to)) {
            return Mono.error(new BadRequestException("From date must be before or equal to to date"));
        }

        LocalDateTime periodStart = from.atStartOfDay();
        LocalDateTime periodEnd = to.plusDays(1).atStartOfDay();

        return SecurityHelper.currentUser()
                .flatMap(authUser -> {
                    Long salonId = SecurityHelper.isSuperAdmin(authUser) ? null : authUser.getSalonId();
                    Long effectiveUserId = SecurityHelper.resolveReportUserId(authUser, userId);
                    Mono<CommissionSummaryRow> summaryMono = commissionReportRepositoryPort.getSummary(
                            salonId, periodStart, periodEnd, branchId, effectiveUserId);
                    Mono<List<CommissionReportUserItem>> byUserMono = commissionReportRepositoryPort
                            .getByUser(salonId, periodStart, periodEnd, branchId, effectiveUserId)
                            .map(this::toUserItem)
                            .collectList();
                    Mono<List<CommissionReportUserServiceItem>> byUserAndServiceMono = commissionReportRepositoryPort
                            .getByUserAndService(salonId, periodStart, periodEnd, branchId, effectiveUserId)
                            .map(this::toUserServiceItem)
                            .collectList();

                    return Mono.zip(summaryMono, byUserMono, byUserAndServiceMono)
                            .map(tuple -> {
                                CommissionSummaryRow summary = tuple.getT1();
                                return new CommissionReportResponse(
                                        from,
                                        to,
                                        summary.getTotalItems() != null ? summary.getTotalItems() : 0L,
                                        normalizeMoney(summary.getTotalRevenue()),
                                        normalizeMoney(summary.getTotalCommission()),
                                        tuple.getT2(),
                                        tuple.getT3()
                                );
                            });
                });
    }

    private CommissionReportUserItem toUserItem(CommissionUserRow row) {
        return new CommissionReportUserItem(
                row.getUserId(),
                row.getUserName(),
                row.getDefaultCommissionPercentage(),
                row.getItemsSold() != null ? row.getItemsSold() : 0L,
                normalizeMoney(row.getRevenue()),
                normalizeMoney(row.getCommissionAmount())
        );
    }

    private CommissionReportUserServiceItem toUserServiceItem(CommissionUserServiceRow row) {
        return new CommissionReportUserServiceItem(
                row.getUserId(),
                row.getUserName(),
                row.getServiceId(),
                row.getServiceName(),
                row.getQuantity() != null ? row.getQuantity() : 0L,
                normalizeMoney(row.getRevenue()),
                row.getAppliedCommissionPercentage(),
                Boolean.TRUE.equals(row.getUsedServiceCommission()),
                normalizeMoney(row.getCommissionAmount())
        );
    }

    private BigDecimal normalizeMoney(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        }
        return value.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}
