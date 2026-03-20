package domain.repository;

import domain.models.Hackathon;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HackathonRepository {
    void save(Hackathon h);
    Optional<Hackathon> findById(UUID id);
    List<Hackathon> findAll();
}
