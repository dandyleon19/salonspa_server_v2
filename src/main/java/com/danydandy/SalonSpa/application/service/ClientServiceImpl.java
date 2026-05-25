package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.domain.model.*;
import com.danydandy.SalonSpa.domain.ports.in.ClientUseCase;
import com.danydandy.SalonSpa.domain.ports.out.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class ClientServiceImpl implements ClientUseCase {

    private final ClientRepositoryPort clientRepositoryPort;
    private final ClinicalRecordRepositoryPort clinicalRecordRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final BranchRepositoryPort branchRepositoryPort;
    private final ClinicalRecordServiceRepositoryPort clinicalRecordServiceRepositoryPort;
    private final ServiceRepositoryPort serviceRepositoryPort;

    @Override
    public Mono<Client> create(Client client) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (AuthUser) ctx.getAuthentication().getPrincipal())
                .flatMap(authUser -> {
                    client.setSalonId(authUser.getSalonId());
                    return clientRepositoryPort.save(client);
                });
    }

    @Override
    public Flux<Client> findAll() {
        return clientRepositoryPort.findAll()
                .flatMap(client -> clinicalRecordRepositoryPort.findByClientId(client.getId())
                        .collectList()
                        .map(clinicalRecords -> {
                            client.setClinicalRecords(clinicalRecords);
                            return client;
                        }));
    }

    @Override
    public Mono<Client> findById(Long id) {
        return clientRepositoryPort.findById(id)
                .flatMap(client -> clinicalRecordRepositoryPort.findByClientId(client.getId())
                        .flatMap(clinicalRecord -> {
                            Mono<List<String>> servicesMono = clinicalRecordServiceRepositoryPort.findByClinicalRecordId(clinicalRecord.getId())
                                            .flatMap(clinicalRecordService -> serviceRepositoryPort.findById(clinicalRecordService.getServiceId()))
                                            .map(Service::getName)
                                            .collectList();
                            return Mono.zip(
                                    userRepositoryPort.findById(clinicalRecord.getUserId()),
                                    branchRepositoryPort.findById(clinicalRecord.getBranchId()),
                                    servicesMono
                            ).map(tuple -> {
                                User user = tuple.getT1();
                                Branch branch = tuple.getT2();
                                List<String> services = tuple.getT3();
                                clinicalRecord.setUserName(user.getFirstName() + " " + user.getLastName());
                                clinicalRecord.setBranchName(branch.getName());
                                clinicalRecord.setAssociatedServices(services);
                                return clinicalRecord;
                            });
                        })
                        .collectList()
                        .map(clinicalRecords -> {
                            client.setClinicalRecords(clinicalRecords);
                            return client;
                        }));
    }

    @Override
    public Mono<Client> update(Long id, Client client) {
        return clientRepositoryPort.findById(id)
                .flatMap(existing -> {
                    existing.setFirstName(client.getFirstName());
                    existing.setLastName(client.getLastName());
                    existing.setEmail(client.getEmail());
                    existing.setPhone(client.getPhone());
                    existing.setDocumentNumber(client.getDocumentNumber());
                    existing.setBirthDate(client.getBirthDate());
                    existing.setGender(client.getGender());
                    return clientRepositoryPort.save(existing);
                });
    }

    @Override
    public Mono<Void> delete(Long id) {
        return clientRepositoryPort.deleteById(id);
    }

    @Override
    public Flux<Client> findBySalonId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (AuthUser) ctx.getAuthentication().getPrincipal())
                .flatMapMany(authUser ->
                        clientRepositoryPort.findBySalonId(authUser.getSalonId())
                );
    }
}
