package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.application.security.SecurityHelper;
import com.danydandy.SalonSpa.application.util.SearchHelper;
import com.danydandy.SalonSpa.domain.exception.NotFoundException;
import com.danydandy.SalonSpa.domain.model.*;
import com.danydandy.SalonSpa.domain.ports.in.AppointmentUseCase;
import com.danydandy.SalonSpa.domain.ports.in.ClientUseCase;
import com.danydandy.SalonSpa.domain.ports.out.*;
import lombok.RequiredArgsConstructor;
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
    private final AppointmentUseCase appointmentUseCase;

    @Override
    public Mono<Client> create(Client client) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> {
                    client.setSalonId(authUser.getSalonId());
                    return clientRepositoryPort.save(client);
                });
    }

    @Override
    public Mono<PageResponse<Client>> findPage(int page, int size, String search) {
        String searchFilter = SearchHelper.toLikePattern(search);
        return SecurityHelper.currentUser()
                .flatMap(authUser -> {
                    if (SecurityHelper.isSuperAdmin(authUser)) {
                        return paginateAll(page, size, searchFilter);
                    }
                    return paginateBySalonId(authUser.getSalonId(), page, size, searchFilter);
                });
    }

    @Override
    public Mono<Client> findById(Long id) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> clientRepositoryPort.findById(id)
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("Client", id)))
                        .flatMap(client -> SecurityHelper.requireSameSalon(client, client.getSalonId(), authUser, "Client", id)));
    }

    @Override
    public Mono<PageResponse<ClinicalRecord>> findClinicalRecordsPage(Long clientId, int page, int size) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> clientRepositoryPort.findById(clientId)
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("Client", clientId)))
                        .flatMap(client -> SecurityHelper.requireSameSalon(client, client.getSalonId(), authUser, "Client", clientId))
                        .flatMap(client -> Mono.zip(
                                clinicalRecordRepositoryPort.countByClientId(client.getId()),
                                clinicalRecordRepositoryPort.findByClientId(client.getId(), page, size)
                                        .flatMap(this::enrichClinicalRecord)
                                        .collectList()
                        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()))));
    }

    @Override
    public Mono<Client> update(Long id, Client client) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> clientRepositoryPort.findById(id)
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("Client", id)))
                        .flatMap(existing -> SecurityHelper.requireSameSalon(existing, existing.getSalonId(), authUser, "Client", id))
                        .flatMap(existing -> {
                            existing.setFirstName(client.getFirstName());
                            existing.setLastName(client.getLastName());
                            existing.setEmail(client.getEmail());
                            existing.setPhone(client.getPhone());
                            existing.setDocumentNumber(client.getDocumentNumber());
                            existing.setBirthDate(client.getBirthDate());
                            existing.setGender(client.getGender());
                            return clientRepositoryPort.save(existing);
                        }));
    }

    @Override
    public Mono<Void> delete(Long id) {
        return SecurityHelper.currentUser()
                .flatMap(authUser -> clientRepositoryPort.findById(id)
                        .switchIfEmpty(Mono.error(NotFoundException.forResource("Client", id)))
                        .flatMap(client -> SecurityHelper.requireSameSalon(client, client.getSalonId(), authUser, "Client", id))
                        .flatMap(client -> clientRepositoryPort.deleteById(id)));
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
                .flatMap(tuple -> {
                    clinicalRecord.setUserName(tuple.getT1());
                    clinicalRecord.setBranchName(tuple.getT2());
                    clinicalRecord.setAssociatedServices(tuple.getT3());
                    if (clinicalRecord.getFollowUpAppointmentId() == null) {
                        return Mono.just(clinicalRecord);
                    }
                    return appointmentUseCase.findById(clinicalRecord.getFollowUpAppointmentId())
                            .map(appointment -> {
                                clinicalRecord.setNextAppointment(appointment);
                                return clinicalRecord;
                            });
                });
    }

    private Mono<PageResponse<Client>> paginateAll(int page, int size, String search) {
        return Mono.zip(
                clientRepositoryPort.countAll(search),
                clientRepositoryPort.findAll(page, size, search).collectList()
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }

    private Mono<PageResponse<Client>> paginateBySalonId(Long salonId, int page, int size, String search) {
        return Mono.zip(
                clientRepositoryPort.countBySalonId(salonId, search),
                clientRepositoryPort.findBySalonId(salonId, page, size, search).collectList()
        ).map(tuple -> PageResponse.of(tuple.getT2(), page, size, tuple.getT1()));
    }
}
