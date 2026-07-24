package domain.repository;

import domain.models.Team;
import java.util.Optional;
import java.util.UUID;

public interface TeamRepository {
    void save(Team team);
    Optional<Team> findById(UUID id);
}