package com.danydandy.SalonSpa.application.service;

import com.danydandy.SalonSpa.domain.model.Client;
import com.danydandy.SalonSpa.domain.ports.in.ClientUseCase;
import com.danydandy.SalonSpa.domain.ports.out.ClientRepositoryPort;
import com.danydandy.SalonSpa.domain.ports.out.ClinicalRecordRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ClientService implements ClientUseCase {

    private final ClientRepositoryPort clientRepositoryPort;
    private final ClinicalRecordRepositoryPort clinicalRecordRepositoryPort;

    @Override
    public Mono<Client> create(Client client) {
        return clientRepositoryPort.save(client);
    }

    @Override
    public Flux<Client> findAll() {
        return clientRepositoryPort.findAll()
                .flatMap(client -> clinicalRecordRepositoryPort.findByClientId(client.getId())
                        .collectList()
                        .map(clinicalRecords -> {
                            client.setClinicalRecords(clinicalRecords);;
                            return client;
                        }));
    }

    @Override
    public Mono<Client> findById(Long id) {
        return clientRepositoryPort.findById(id);
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
    public Flux<Client> findBySalonId(Long id) {
        return clientRepositoryPort.findBySalonId(id);
    }
}
