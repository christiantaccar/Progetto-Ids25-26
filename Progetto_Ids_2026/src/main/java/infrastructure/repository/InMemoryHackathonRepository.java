package infrastructure.repository;

import domain.models.Hackathon;
import domain.repository.HackathonRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.util.*;

public class InMemoryHackathonRepository implements HackathonRepository {

    private final Map<UUID, Hackathon> storage = new HashMap<>();
    private final Clock clock;

    public InMemoryHackathonRepository() {
        this(Clock.systemDefaultZone());
    }

    public InMemoryHackathonRepository(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "Clock non può essere null");
    }

    @Override
    public void save(Hackathon h) {
        Objects.requireNonNull(h, "Hackathon non può essere null");
        storage.put(h.getId(), h);
    }

    @Override
    public Optional<Hackathon> findById(UUID id) {
        Objects.requireNonNull(id, "ID non può essere null");
        Hackathon h = storage.get(id);
        if (h != null) {
            h.aggiornaStato(LocalDate.now(clock));
        }
        return Optional.ofNullable(h);
    }

    @Override
    public List<Hackathon> findAll() {
        storage.values().forEach(h -> h.aggiornaStato(LocalDate.now(clock)));
        return List.copyOf(storage.values());
    }
}