package com.danydandy.SalonSpa.infrastructure.adapter.out.repository;

import com.danydandy.SalonSpa.domain.model.Client;
import com.danydandy.SalonSpa.domain.ports.out.ClientRepositoryPort;
import com.danydandy.SalonSpa.infrastructure.adapter.out.mapper.ClientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ClientRepositoryAdapter implements ClientRepositoryPort {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Override
    public Mono<Client> save(Client client) {
        return clientRepository.save(clientMapper.toEntity(client))
                .map(clientMapper::toDomain);
    }

    @Override
    public Flux<Client> findAll(int page, int size) {
        return clientRepository.findAllByOrderByCreatedAtAsc(PageRequest.of(page, size))
                .map(clientMapper::toDomain);
    }

    @Override
    public Mono<Long> countAll() {
        return clientRepository.count();
    }

    @Override
    public Mono<Client> findById(Long id) {
        return clientRepository.findById(id)
                .map(clientMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return clientRepository.deleteById(id);
    }

    @Override
    public Flux<Client> findBySalonId(Long salonId, int page, int size) {
        return clientRepository.findBySalonIdOrderByCreatedAtAsc(salonId, PageRequest.of(page, size))
                .map(clientMapper::toDomain);
    }

    @Override
    public Mono<Long> countBySalonId(Long salonId) {
        return clientRepository.countBySalonId(salonId);
    }
}
