package infrastructure.repository;

import domain.models.Team;
import domain.repository.TeamRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class InMemoryTeamRepository implements TeamRepository {
    private final Map<UUID, Team> storage = new HashMap<>();

    @Override
    public void save(Team team) {
        Objects.requireNonNull(team);
        storage.put(team.getId(), team);
    }

    @Override
    public Optional<Team> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }
}