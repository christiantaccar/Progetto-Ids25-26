package domain.repository;

import domain.models.Sottomissione;

import java.util.Optional;
import java.util.UUID;

public interface SottomissioneRepository {
    void save(Sottomissione s);
    Optional<Sottomissione> findById(UUID id);
}