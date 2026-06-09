package com.danydandy.SalonSpa.domain.ports.in;

import com.danydandy.SalonSpa.application.dto.response.PageResponse;
import com.danydandy.SalonSpa.domain.model.Client;
import com.danydandy.SalonSpa.domain.model.ClinicalRecord;
import reactor.core.publisher.Mono;

public interface ClientUseCase {
    Mono<Client> create(Client client);
    Mono<PageResponse<Client>> findPage(int page, int size);
    Mono<Client> findById(Long id);
    Mono<PageResponse<ClinicalRecord>> findClinicalRecordsPage(Long clientId, int page, int size);
    Mono<Client> update(Long id, Client client);
    Mono<Void> delete(Long id);
}
