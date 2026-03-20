package infrastructure.repository;

import domain.models.Hackathon;
import domain.repository.HackathonRepository;

import java.util.*;

public class InMemoryHackathonRepository implements HackathonRepository {

    private final Map<UUID, Hackathon> storage = new HashMap<>();

    @Override
    public void save(Hackathon h) {
        Objects.requireNonNull(h, "Hackathon non può essere null");
        storage.put(h.getId(), h);
    }

    @Override
    public Optional<Hackathon> findById(UUID id) {
        Objects.requireNonNull(id, "ID non può essere null");
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Hackathon> findAll() {
        return List.copyOf(storage.values());
    }
}