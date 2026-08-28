package infrastructure.repository;

import domain.models.Sottomissione;
import domain.repository.SottomissioneRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class InMemorySottomissioneRepository implements SottomissioneRepository {

    private final Map<UUID, Sottomissione> storage = new HashMap<>();

    @Override
    public void save(Sottomissione s) {
        Objects.requireNonNull(s, "Sottomissione non può essere null");
        storage.put(s.getId(), s);
    }

    @Override
    public Optional<Sottomissione> findById(UUID id) {
        Objects.requireNonNull(id, "ID non può essere null");
        return Optional.ofNullable(storage.get(id));
    }
}