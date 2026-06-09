package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.domain.exception.NotFoundException;
import com.danydandy.SalonSpa.domain.model.*;
import com.danydandy.SalonSpa.domain.ports.in.ClientUseCase;
import com.danydandy.SalonSpa.domain.ports.out.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
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
    public Mono<PageResponse<Client>> findPage(int page, int size) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (AuthUser) ctx.getAuthentication().getPrincipal())
                .flatMap(authUser -> {
                    if ("SUPER_ADMIN".equals(authUser.getRole())) {
                        return paginateAll(page, size);
                    }
                    return paginateBySalonId(authUser.getSalonId(), page, size);
                });
    }

    @Override
    public Mono<Client> findById(Long id) {
        return clientRepositoryPort.findById(id)
                .switchIfEmpty(Mono.error(NotFoundException.forResource("Client", id)));
    }

    @Override
    public Mono<PageResponse<ClinicalRecord>> findClinicalRecordsPage(Long clientId, int page, int size) {
        return clientRepositoryPort.findById(clientId)
                .switchIfEmpty(Mono.error(NotFoundException.forResource("Client", clientId)))
                .flatMap(client -> Mono.zip(
                        clinicalRecordRepositoryPort.countByClientId(client.getId()),
                        clinicalRecordRepositoryPort.findByClientId(client.getId(), page, size)
                                .flatMap(this::enrichClinicalRecord)
                                .collectList()
                ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1())));
    }

    @Override
    public Mono<Client> update(Long id, Client client) {
        return clientRepositoryPort.findById(id)
                .switchIfEmpty(Mono.error(NotFoundException.forResource("Client", id)))
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
        return clientRepositoryPort.findById(id)
                .switchIfEmpty(Mono.error(NotFoundException.forResource("Client", id)))
                .flatMap(client -> clientRepositoryPort.deleteById(id));
    }

    private Mono<ClinicalRecord> enrichClinicalRecord(ClinicalRecord clinicalRecord) {
        Mono<List<String>> servicesMono = clinicalRecordServiceRepositoryPort
                .findByClinicalRecordId(clinicalRecord.getId())
                .flatMap(clinicalRecordService -> serviceRepositoryPort
                        .findById(clinicalRecordService.getServiceId()))
                .map(Service::getName)
                .collectList();

        Mono<String> userNameMono = clinicalRecord.getUserId() != null
                ? userRepositoryPort.findById(clinicalRecord.getUserId())
                        .map(user -> user.getFirstName() + " " + user.getLastName())
                : Mono.just("");

        Mono<String> branchNameMono = clinicalRecord.getBranchId() != null
                ? branchRepositoryPort.findById(clinicalRecord.getBranchId()).map(Branch::getName)
                : Mono.just("");

        return Mono.zip(userNameMono, branchNameMono, servicesMono)
                .map(tuple -> {
                    clinicalRecord.setUserName(tuple.getT1());
                    clinicalRecord.setBranchName(tuple.getT2());
                    clinicalRecord.setAssociatedServices(tuple.getT3());
                    return clinicalRecord;
                });
    }

    private Mono<PageResponse<Client>> paginateAll(int page, int size) {
        return Mono.zip(
                clientRepositoryPort.countAll(),
                clientRepositoryPort.findAll(page, size).collectList()
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }

    private Mono<PageResponse<Client>> paginateBySalonId(Long salonId, int page, int size) {
        return Mono.zip(
                clientRepositoryPort.countBySalonId(salonId),
                clientRepositoryPort.findBySalonId(salonId, page, size).collectList()
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }
}
