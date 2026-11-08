package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.application.dto.response.*;
import com.danydandy.SalonSpa.application.security.SecurityHelper;
import com.danydandy.SalonSpa.domain.exception.BadRequestException;
import com.danydandy.SalonSpa.domain.exception.NotFoundException;
import com.danydandy.SalonSpa.domain.model.*;
import com.danydandy.SalonSpa.domain.ports.in.SaleUseCase;
import com.danydandy.SalonSpa.domain.ports.out.*;
import com.danydandy.SalonSpa.infrastructure.adapter.out.repository.*;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class SaleServiceImpl implements SaleUseCase {

    private static final int MONEY_SCALE = 2;

    private final SaleRepositoryPort saleRepositoryPort;
    private final SaleItemRepositoryPort saleItemRepositoryPort;
    private final SalePaymentRepositoryPort salePaymentRepositoryPort;
    private final SaleReportRepositoryPort saleReportRepositoryPort;
    private final ClientRepositoryPort clientRepositoryPort;
    private final BranchRepositoryPort branchRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final ServiceRepositoryPort serviceRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final ProductStockMovementRepositoryPort productStockMovementRepositoryPort;
    private final AppointmentRepositoryPort appointmentRepositoryPort;
    private final SaleEnricher saleEnricher;

    @Override
    public Mono<Sale> create(Sale sale, List<SaleItem> items, List<SalePayment> payments) {
        Map<SaleItem, Integer> resultingStockByItem = new ConcurrentHashMap<>();
        return SecurityHelper.currentUser()
                .flatMap(authUser -> validateReferences(sale, items, authUser)
                        .flatMap(salonId -> buildLineItems(items, salonId, sale.getClientId(), resultingStockByItem)
                                .collectList()
                                .flatMap(builtItems -> {
                                    BigDecimal subtotal = sumLineTotals(builtItems);
                                    BigDecimal discountAmount = normalizeMoney(sale.getDiscountAmount());
                                    if (discountAmount.compareTo(subtotal) > 0) {
                                        return Mono.error(new BadRequestException(
                                                "El descuento no puede superar el subtotal"));
                                    }
                                    BigDecimal totalAmount = subtotal.subtract(discountAmount);
                                    BigDecimal amountPaid = sumPayments(payments);
                                    if (amountPaid.compareTo(totalAmount) > 0) {
                                        return Mono.error(new BadRequestException(
                                                "La suma de los pagos no puede superar el total de la venta"));
                                    }

                                    sale.setSalonId(salonId);
                                    sale.setRegisteredByUserId(authUser.getUserId());
                                    sale.setSubtotal(subtotal);
                                    sale.setDiscountAmount(discountAmount);
                                    sale.setTotalAmount(totalAmount);
                                    sale.setAmountPaid(amountPaid);
                                    sale.setStatus(resolveStatus(totalAmount, amountPaid));
                                    if (sale.getSoldAt() == null) {
                                        sale.setSoldAt(LocalDateTime.now());
                                    }

                                    return saleRepositoryPort.save(sale)
                                            .flatMap(saved -> saveItems(saved.getId(), builtItems, resultingStockByItem, authUser.getUserId())
                                                    .then(savePayments(saved.getId(), payments))
                                                    .thenReturn(saved));
                                }))
                        .flatMap(saleEnricher::enrich));
    }

    @Override
    public Mono<PageResponse<Sale>> findPage(int page, int size, Long branchId, Long clientId, SaleStatus status,
                                             LocalDate from, LocalDate to) {
        String statusFilter = status != null ? status.name() : null;
        return SecurityHelper.currentUser()
                .flatMap(authUser -> {
                    if (SecurityHelper.isSuperAdmin(authUser)) {
                        return paginateAll(page, size, branchId, clientId, statusFilter, from, to);
                    }
                    return paginateBySalonId(authUser.getSalonId(), page, size, branchId, clientId, statusFilter, from,
                            to);
                });
    }

    @Override
    public Mono<Sale> findById(Long id) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> loadWithTenantCheck(id, authUser)
                        .flatMap(saleEnricher::enrich));
    }

    @Override
    public Mono<Sale> addPayment(Long saleId, SalePayment payment) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> loadWithTenantCheck(saleId, authUser)
                        .flatMap(sale -> {
                            if (sale.getStatus() == SaleStatus.CANCELLED) {
                                return Mono.error(new BadRequestException("No se pueden agregar pagos a una venta cancelada"));
                            }
                            BigDecimal remaining = sale.getTotalAmount().subtract(sale.getAmountPaid());
                            if (payment.getAmount().compareTo(remaining) > 0) {
                                return Mono.error(new BadRequestException(
                                        "El pago supera el saldo pendiente"));
                            }
                            if (payment.getPaidAt() == null) {
                                payment.setPaidAt(LocalDateTime.now());
                            }
                            payment.setSaleId(saleId);
                            return salePaymentRepositoryPort.save(payment)
                                    .flatMap(savedPayment -> {
                                        sale.setAmountPaid(sale.getAmountPaid().add(savedPayment.getAmount()));
                                        sale.setStatus(resolveStatus(sale.getTotalAmount(), sale.getAmountPaid()));
                                        return saleRepositoryPort.save(sale);
                                    });
                        })
                        .flatMap(saleEnricher::enrich));
    }

    @Override
    public Mono<Sale> cancel(Long id, String reason) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> loadWithTenantCheck(id, authUser)
                        .flatMap(sale -> {
                            if (sale.getStatus() == SaleStatus.CANCELLED) {
                                return Mono.error(new BadRequestException("La venta ya está cancelada"));
                            }
                            sale.setStatus(SaleStatus.CANCELLED);
                            sale.setCancelledAt(LocalDateTime.now());
                            sale.setCancellationReason(reason);
                            return restoreProductStock(id, authUser.getUserId())
                                    .then(saleRepositoryPort.save(sale));
                        })
                        .flatMap(saleEnricher::enrich));
    }

    private Mono<Void> restoreProductStock(Long saleId, Long userId) {
        return saleItemRepositoryPort.findBySaleId(saleId)
                .filter(item -> item.getProductId() != null && item.getQuantity() != null)
                .concatMap(item -> productRepositoryPort.applyStockDelta(item.getProductId(), item.getQuantity())
                        .flatMap(resultingStock -> productStockMovementRepositoryPort.save(ProductStockMovement.builder()
                                .productId(item.getProductId())
                                .movementType(StockMovementType.SALE_CANCELLED)
                                .quantityDelta(item.getQuantity())
                                .resultingStock(resultingStock)
                                .saleId(saleId)
                                .createdByUserId(userId)
                                .build())))
                .then();
    }

    @Override
    public Mono<SaleReportResponse> getReport(LocalDate from, LocalDate to, Long branchId, Long userId) {
        if (from == null || to == null) {
            return Mono.error(new BadRequestException("Las fechas desde y hasta son obligatorias"));
        }
        if (from.isAfter(to)) {
            return Mono.error(new BadRequestException("La fecha desde no puede ser posterior a la fecha hasta"));
        }

        LocalDateTime periodStart = from.atStartOfDay();
        LocalDateTime periodEnd = to.plusDays(1).atStartOfDay();

        return SecurityHelper.currentUser()
                .flatMap(authUser -> {
                    Long salonId = SecurityHelper.isSuperAdmin(authUser) ? null : authUser.getSalonId();
                    Long effectiveUserId = SecurityHelper.resolveReportUserId(authUser, userId);
                    Mono<SaleSummaryRow> summaryMono = saleReportRepositoryPort.getSummary(salonId, periodStart,
                            periodEnd, branchId, effectiveUserId);
                    Mono<Map<String, BigDecimal>> paymentMethodsMono = saleReportRepositoryPort
                            .getByPaymentMethod(salonId, periodStart, periodEnd, branchId, effectiveUserId)
                            .collectMap(SalePaymentMethodRow::getPaymentMethod, SalePaymentMethodRow::getTotalAmount,
                                    LinkedHashMap::new);
                    Mono<List<SaleReportServiceItem>> servicesMono = saleReportRepositoryPort
                            .getByService(salonId, periodStart, periodEnd, branchId, effectiveUserId)
                            .map(row -> new SaleReportServiceItem(
                                    row.getServiceId(),
                                    row.getServiceName(),
                                    row.getQuantity() != null ? row.getQuantity() : 0L,
                                    normalizeMoney(row.getRevenue())
                            ))
                            .collectList();
                    Mono<List<SaleReportProfessionalItem>> professionalsMono = saleReportRepositoryPort
                            .getByProfessional(salonId, periodStart, periodEnd, branchId, effectiveUserId)
                            .flatMap(this::toProfessionalReportItem)
                            .collectList();
                    Mono<List<SaleReportDailyItem>> dailyMono = saleReportRepositoryPort
                            .getDailyBreakdown(salonId, periodStart, periodEnd, branchId, effectiveUserId)
                            .map(row -> new SaleReportDailyItem(
                                    row.getDate(),
                                    row.getSalesCount() != null ? row.getSalesCount() : 0L,
                                    normalizeMoney(row.getRevenue())
                            ))
                            .collectList();

                    return Mono.zip(summaryMono, paymentMethodsMono, servicesMono, professionalsMono, dailyMono)
                            .map(tuple -> {
                                SaleSummaryRow summary = tuple.getT1();
                                return new SaleReportResponse(
                                        from,
                                        to,
                                        summary.getTotalSales() != null ? summary.getTotalSales() : 0L,
                                        normalizeMoney(summary.getTotalRevenue()),
                                        normalizeMoney(summary.getTotalPaid()),
                                        normalizeMoney(summary.getTotalOutstanding()),
                                        tuple.getT2(),
                                        tuple.getT3(),
                                        tuple.getT4(),
                                        tuple.getT5()
                                );
                            });
                });
    }

    private Mono<Long> validateReferences(Sale sale, List<SaleItem> items, AuthUser authUser) {
        if (items == null || items.isEmpty()) {
            return Mono.error(new BadRequestException("Se necesita al menos un ítem en la venta"));
        }
        return clientRepositoryPort.findById(sale.getClientId())
                .switchIfEmpty(Mono.error(NotFoundException.forResource("Client", sale.getClientId())))
                .flatMap(client -> SecurityHelper.requireSameSalon(client, client.getSalonId(), authUser, "Client",
                        sale.getClientId()))
                .flatMap(client -> branchRepositoryPort.findById(sale.getBranchId())
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("Branch", sale.getBranchId())))
                        .flatMap(branch -> {
                            if (!client.getSalonId().equals(branch.getSalonId())) {
                                return Mono.error(new BadRequestException(
                                        "La sucursal no pertenece al mismo salón que el cliente"));
                            }
                            return Mono.just(client.getSalonId());
                        }))
                .flatMap(salonId -> validateAppointment(sale.getAppointmentId(), salonId, sale.getClientId()));
    }

    private Mono<Long> validateAppointment(Long appointmentId, Long salonId, Long clientId) {
        if (appointmentId == null) {
            return Mono.just(salonId);
        }
        return appointmentRepositoryPort.findById(appointmentId)
                .switchIfEmpty(Mono.error(NotFoundException.forResource("Appointment", appointmentId)))
                .flatMap(appointment -> {
                    if (!salonId.equals(appointment.getSalonId())) {
                        return Mono.error(NotFoundException.forResource("Appointment", appointmentId));
                    }
                    if (!clientId.equals(appointment.getClientId())) {
                        return Mono.error(new BadRequestException("La cita no pertenece al mismo cliente"));
                    }
                    return Mono.just(salonId);
                });
    }

    private Flux<SaleItem> buildLineItems(List<SaleItem> items, Long salonId, Long clientId,
                                           Map<SaleItem, Integer> resultingStockByItem) {
        return Flux.fromIterable(items)
                .concatMap(item -> {
                    boolean hasService = item.getServiceId() != null;
                    boolean hasProduct = item.getProductId() != null;
                    if (hasService == hasProduct) {
                        return Mono.<SaleItem>error(new BadRequestException(
                                "Cada ítem de la venta debe ser un servicio o un producto, no ambos"));
                    }
                    if (hasProduct) {
                        return buildProductLineItem(item, salonId, resultingStockByItem);
                    }
                    return buildServiceLineItem(item, salonId, clientId);
                });
    }

    private Mono<SaleItem> buildProductLineItem(SaleItem item, Long salonId,
                                                 Map<SaleItem, Integer> resultingStockByItem) {
        return productRepositoryPort.findById(item.getProductId())
                .switchIfEmpty(Mono.error(NotFoundException.forResource("Product", item.getProductId())))
                .flatMap(product -> {
                    if (!salonId.equals(product.getSalonId())) {
                        return Mono.error(new BadRequestException("El producto no pertenece al mismo salón"));
                    }
                    if (Boolean.FALSE.equals(product.getIsActive())) {
                        return Mono.error(new BadRequestException("El producto no está activo: " + product.getName()));
                    }

                    int quantity = item.getQuantity() != null && item.getQuantity() > 0
                            ? item.getQuantity() : 1;
                    if (product.getStockQuantity() == null || product.getStockQuantity() < quantity) {
                        return Mono.error(new BadRequestException(
                                "Stock insuficiente del producto: " + product.getName()));
                    }

                    BigDecimal unitPrice = item.getUnitPrice() != null
                            ? normalizeMoney(item.getUnitPrice())
                            : normalizeMoney(product.getPrice());
                    BigDecimal lineDiscount = normalizeMoney(item.getDiscountAmount());
                    BigDecimal lineSubtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
                    if (lineDiscount.compareTo(lineSubtotal) > 0) {
                        return Mono.error(new BadRequestException(
                                "El descuento del ítem no puede superar su subtotal en el producto " + product.getName()));
                    }
                    BigDecimal lineTotal = lineSubtotal.subtract(lineDiscount);

                    return productRepositoryPort.applyStockDelta(product.getId(), -quantity)
                            .switchIfEmpty(Mono.error(new BadRequestException(
                                    "Stock insuficiente del producto: " + product.getName())))
                            .map(resultingStock -> {
                                SaleItem built = SaleItem.builder()
                                        .productId(product.getId())
                                        .productName(product.getName())
                                        .appointmentId(item.getAppointmentId())
                                        .quantity(quantity)
                                        .unitPrice(unitPrice)
                                        .discountAmount(lineDiscount)
                                        .lineTotal(lineTotal)
                                        .build();
                                resultingStockByItem.put(built, resultingStock);
                                return built;
                            });
                });
    }

    private Mono<SaleItem> buildServiceLineItem(SaleItem item, Long salonId, Long clientId) {
        if (item.getUserId() == null) {
            return Mono.error(new BadRequestException("Indica el profesional de cada servicio"));
        }
        return validateItemReferences(item, salonId, clientId)
                        .flatMap(service -> {
                            int quantity = item.getQuantity() != null && item.getQuantity() > 0
                                    ? item.getQuantity() : 1;
                            BigDecimal unitPrice = item.getUnitPrice() != null
                                    ? normalizeMoney(item.getUnitPrice())
                                    : normalizeMoney(service.getPrice());
                            if (unitPrice == null) {
                                return Mono.error(new BadRequestException(
                                        "Falta el precio unitario del servicio " + service.getId()));
                            }
                            BigDecimal lineDiscount = normalizeMoney(item.getDiscountAmount());
                            BigDecimal lineSubtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
                            if (lineDiscount.compareTo(lineSubtotal) > 0) {
                                return Mono.error(new BadRequestException(
                                        "El descuento del ítem no puede superar su subtotal en el servicio "
                                                + service.getName()));
                            }
                            BigDecimal lineTotal = lineSubtotal.subtract(lineDiscount);

                            SaleItem built = SaleItem.builder()
                                    .serviceId(service.getId())
                                    .userId(item.getUserId())
                                    .appointmentId(item.getAppointmentId())
                                    .serviceName(service.getName())
                                    .quantity(quantity)
                                    .unitPrice(unitPrice)
                                    .discountAmount(lineDiscount)
                                    .lineTotal(lineTotal)
                                    .build();
                            return Mono.just(built);
                        });
    }

    private Mono<Service> validateItemReferences(SaleItem item, Long salonId, Long clientId) {
        return userRepositoryPort.findById(item.getUserId())
                .switchIfEmpty(Mono.error(NotFoundException.forResource("User", item.getUserId())))
                .flatMap(user -> {
                    if (!salonId.equals(user.getSalonId())) {
                        return Mono.error(new BadRequestException("El profesional no pertenece al mismo salón"));
                    }
                    return serviceRepositoryPort.findById(item.getServiceId())
                            .switchIfEmpty(Mono.error(NotFoundException.forResource("Service", item.getServiceId())))
                            .flatMap(service -> {
                                if (!salonId.equals(service.getSalonId())) {
                                    return Mono.error(new BadRequestException(
                                            "El servicio no pertenece al mismo salón"));
                                }
                                if (Boolean.FALSE.equals(service.getIsActive())) {
                                    return Mono.error(new BadRequestException(
                                            "El servicio no está activo: " + service.getName()));
                                }
                                return validateAppointment(item.getAppointmentId(), salonId, clientId)
                                        .thenReturn(service);
                            });
                });
    }

    private Mono<Void> saveItems(Long saleId, List<SaleItem> items, Map<SaleItem, Integer> resultingStockByItem,
                                  Long userId) {
        return Flux.fromIterable(items)
                .concatMap(item -> {
                    Integer resultingStock = resultingStockByItem.get(item);
                    item.setSaleId(saleId);
                    return saleItemRepositoryPort.save(item)
                            .flatMap(saved -> {
                                if (item.getProductId() == null || resultingStock == null) {
                                    return Mono.just(saved);
                                }
                                return productStockMovementRepositoryPort.save(ProductStockMovement.builder()
                                                .productId(item.getProductId())
                                                .movementType(StockMovementType.SALE)
                                                .quantityDelta(-item.getQuantity())
                                                .resultingStock(resultingStock)
                                                .saleId(saleId)
                                                .createdByUserId(userId)
                                                .build())
                                        .thenReturn(saved);
                            });
                })
                .then();
    }

    private Mono<Void> savePayments(Long saleId, List<SalePayment> payments) {
        if (payments == null || payments.isEmpty()) {
            return Mono.empty();
        }
        return Flux.fromIterable(payments)
                .concatMap(payment -> {
                    payment.setSaleId(saleId);
                    if (payment.getPaidAt() == null) {
                        payment.setPaidAt(LocalDateTime.now());
                    }
                    return salePaymentRepositoryPort.save(payment);
                })
                .then();
    }

    private Mono<Sale> loadWithTenantCheck(Long id, AuthUser authUser) {
        return saleRepositoryPort.findById(id)
                .switchIfEmpty(Mono.error(NotFoundException.forResource("Sale", id)))
                .flatMap(sale -> SecurityHelper.requireSameSalon(sale, sale.getSalonId(), authUser, "Sale", id));
    }

    private Mono<PageResponse<Sale>> paginateAll(int page, int size, Long branchId, Long clientId, String status,
                                                 LocalDate from, LocalDate to) {
        return Mono.zip(
                saleRepositoryPort.countAll(branchId, clientId, status, from, to),
                saleRepositoryPort.findAll(page, size, branchId, clientId, status, from, to)
                        .flatMap(saleEnricher::enrichSummary)
                        .collectList()
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }

    private Mono<PageResponse<Sale>> paginateBySalonId(Long salonId, int page, int size, Long branchId, Long clientId,
                                                       String status, LocalDate from, LocalDate to) {
        return Mono.zip(
                saleRepositoryPort.countBySalonId(salonId, branchId, clientId, status, from, to),
                saleRepositoryPort.findBySalonId(salonId, page, size, branchId, clientId, status, from, to)
                        .flatMap(saleEnricher::enrichSummary)
                        .collectList()
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }

    private Mono<SaleReportProfessionalItem> toProfessionalReportItem(SaleProfessionalRow row) {
        if (row.getUserId() == null) {
            return Mono.just(new SaleReportProfessionalItem(null, "", 0L, BigDecimal.ZERO));
        }
        return userRepositoryPort.findById(row.getUserId())
                .map(user -> new SaleReportProfessionalItem(
                        row.getUserId(),
                        user.getFirstName() + " " + user.getLastName(),
                        row.getItemsSold() != null ? row.getItemsSold() : 0L,
                        normalizeMoney(row.getRevenue())
                ))
                .defaultIfEmpty(new SaleReportProfessionalItem(
                        row.getUserId(),
                        "",
                        row.getItemsSold() != null ? row.getItemsSold() : 0L,
                        normalizeMoney(row.getRevenue())
                ));
    }

    private BigDecimal sumLineTotals(List<SaleItem> items) {
        return items.stream()
                .map(SaleItem::getLineTotal)
                .map(this::normalizeMoney)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal sumPayments(List<SalePayment> payments) {
        if (payments == null || payments.isEmpty()) {
            return BigDecimal.ZERO.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        }
        return payments.stream()
                .map(SalePayment::getAmount)
                .map(this::normalizeMoney)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    private SaleStatus resolveStatus(BigDecimal totalAmount, BigDecimal amountPaid) {
        if (amountPaid.compareTo(totalAmount) >= 0) {
            return SaleStatus.COMPLETED;
        }
        return SaleStatus.PARTIALLY_PAID;
    }

    private BigDecimal normalizeMoney(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        }
        return value.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}
