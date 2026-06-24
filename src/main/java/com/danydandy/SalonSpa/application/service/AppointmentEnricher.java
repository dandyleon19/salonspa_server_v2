package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.domain.model.Appointment;
import com.danydandy.SalonSpa.domain.model.Branch;
import com.danydandy.SalonSpa.domain.model.Service;
import com.danydandy.SalonSpa.domain.ports.out.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AppointmentEnricher {

    private final ClientRepositoryPort clientRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final BranchRepositoryPort branchRepositoryPort;
    private final ServiceRepositoryPort serviceRepositoryPort;

    public Mono<Appointment> enrich(Appointment appointment) {
        Mono<Appointment> withClientMono = clientRepositoryPort.findById(appointment.getClientId())
                .map(client -> {
                    appointment.setClientName(client.getFirstName() + " " + client.getLastName());
                    appointment.setClientPhone(client.getPhone());
                    appointment.setClientEmail(client.getEmail());
                    appointment.setClientBirthDate(client.getBirthDate());
                    appointment.setClientGender(client.getGender());
                    return appointment;
                })
                .defaultIfEmpty(appointment)
                .map(apt -> {
                    if (apt.getClientName() == null) {
                        apt.setClientName("");
                    }
                    return apt;
                });

        Mono<String> userNameMono = userRepositoryPort.findById(appointment.getUserId())
                .map(user -> user.getFirstName() + " " + user.getLastName())
                .defaultIfEmpty("");

        Mono<String> branchNameMono = branchRepositoryPort.findById(appointment.getBranchId())
                .map(Branch::getName)
                .defaultIfEmpty("");

        Mono<String> serviceNameMono = appointment.getServiceId() != null
                ? serviceRepositoryPort.findById(appointment.getServiceId()).map(Service::getName).defaultIfEmpty("")
                : Mono.just("");

        return withClientMono.flatMap(apt -> Mono.zip(userNameMono, branchNameMono, serviceNameMono)
                .map(tuple -> {
                    apt.setUserName(tuple.getT1());
                    apt.setBranchName(tuple.getT2());
                    apt.setServiceName(tuple.getT3());
                    return apt;
                }));
    }
}
