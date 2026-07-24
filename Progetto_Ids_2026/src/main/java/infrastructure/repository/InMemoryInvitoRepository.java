package infrastructure.repository;

import domain.models.Invito;
import domain.models.Utente;
import domain.repository.InvitoRepository;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryInvitoRepository implements InvitoRepository {
    private final Map<UUID, Invito> storage = new HashMap<>();

    @Override
    public void save(Invito invito) {
        Objects.requireNonNull(invito);
        storage.put(invito.getId(), invito);
    }

    @Override
    public Optional<Invito> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Invito> findPendentiPerUtente(Utente utente) {
        return storage.values().stream()
                .filter(i -> i.getDestinatario().equals(utente))
                .filter(Invito::isPendente)
                .collect(Collectors.toList());
    }
}