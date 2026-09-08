package domain.repository;

import domain.models.Utente;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UtenteRepository {
    void save(Utente utente);
    Optional<Utente> findById(UUID id);
    List<Utente> findAll();
}
