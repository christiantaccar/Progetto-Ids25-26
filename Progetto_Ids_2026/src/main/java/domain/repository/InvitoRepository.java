package domain.repository;

import domain.models.Invito;
import domain.models.Utente;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvitoRepository {
    void save(Invito invito);
    Optional<Invito> findById(UUID id);
    List<Invito> findPendentiPerUtente(Utente utente);
}