package infrastructure.repository;

import domain.models.Utente;
import domain.repository.UtenteRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class InMemoryUtenteRepository implements UtenteRepository {

    private final Map<UUID, Utente> storage = new HashMap<>();

    @Override
    public void save(Utente utente) {
        Objects.requireNonNull(utente);
        storage.put(utente.getId(), utente);
    }

    @Override
    public Optional<Utente> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }
}
