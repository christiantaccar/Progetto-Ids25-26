package application;

import domain.models.Invito;
import domain.models.Utente;
import domain.repository.InvitoRepository;

import java.util.List;
import java.util.Objects;

public class VisualizzaInvitiService {

    private final InvitoRepository invitoRepository;

    public VisualizzaInvitiService(InvitoRepository invitoRepository) {
        this.invitoRepository = Objects.requireNonNull(invitoRepository);
    }

    public List<Invito> execute(Utente utente) {
        Objects.requireNonNull(utente, "Utente obbligatorio");
        return invitoRepository.findPendentiPerUtente(utente);
    }
}