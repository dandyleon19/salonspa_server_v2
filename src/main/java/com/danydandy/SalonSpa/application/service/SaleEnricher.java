package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.domain.model.*;
import com.danydandy.SalonSpa.domain.ports.out.*;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class SaleEnricher {

    private final ClientRepositoryPort clientRepositoryPort;
    private final BranchRepositoryPort branchRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final SaleItemRepositoryPort saleItemRepositoryPort;
    private final SalePaymentRepositoryPort salePaymentRepositoryPort;

    public Mono<Sale> enrich(Sale sale) {
        Mono<String> clientNameMono = clientRepositoryPort.findById(sale.getClientId())
                .map(client -> client.getFirstName() + " " + client.getLastName())
                .defaultIfEmpty("");

        Mono<String> branchNameMono = branchRepositoryPort.findById(sale.getBranchId())
                .map(Branch::getName)
                .defaultIfEmpty("");

        Mono<String> registeredByNameMono = userRepositoryPort.findById(sale.getRegisteredByUserId())
                .map(user -> user.getFirstName() + " " + user.getLastName())
                .defaultIfEmpty("");

        Mono<List<SaleItem>> itemsMono = saleItemRepositoryPort.findBySaleId(sale.getId())
                .flatMap(this::enrichItem)
                .collectList();

        Mono<List<SalePayment>> paymentsMono = salePaymentRepositoryPort.findBySaleId(sale.getId())
                .collectList();

        return Mono.zip(clientNameMono, branchNameMono, registeredByNameMono, itemsMono, paymentsMono)
                .map(tuple -> {
                    sale.setClientName(tuple.getT1());
                    sale.setBranchName(tuple.getT2());
                    sale.setRegisteredByUserName(tuple.getT3());
                    sale.setItems(tuple.getT4());
                    sale.setPayments(tuple.getT5());
                    return sale;
                });
    }

    public Mono<Sale> enrichSummary(Sale sale) {
        Mono<String> clientNameMono = clientRepositoryPort.findById(sale.getClientId())
                .map(client -> client.getFirstName() + " " + client.getLastName())
                .defaultIfEmpty("");

        Mono<String> branchNameMono = branchRepositoryPort.findById(sale.getBranchId())
                .map(Branch::getName)
                .defaultIfEmpty("");

        return Mono.zip(clientNameMono, branchNameMono)
                .map(tuple -> {
                    sale.setClientName(tuple.getT1());
                    sale.setBranchName(tuple.getT2());
                    return sale;
                });
    }

    private Mono<SaleItem> enrichItem(SaleItem item) {
        if (item.getUserId() == null) {
            return Mono.just(item);
        }
        return userRepositoryPort.findById(item.getUserId())
                .map(user -> {
                    item.setUserName(user.getFirstName() + " " + user.getLastName());
                    return item;
                })
                .defaultIfEmpty(item);
    }
}
