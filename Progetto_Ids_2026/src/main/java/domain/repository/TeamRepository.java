package domain.repository;

import domain.models.Team;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamRepository {
    void save(Team team);
    Optional<Team> findById(UUID id);

    /**
     * Elimina il team. Usato quando il capo team lascia un team di cui
     * era l'unico membro: senza componenti il team non ha più senso ed
     * è quindi sciolto.
     */
    void delete(UUID id);
    List<Team> findAll();
}
