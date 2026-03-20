package application;

import domain.models.Hackathon;
import domain.models.MembroStaff;
import domain.repository.HackathonRepository;
import domain.enums.RuoloStaff;

import java.util.Objects;
import java.util.UUID;

public class AssegnaMentoriService {

    private final HackathonRepository repository;

    public AssegnaMentoriService(HackathonRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public void execute(MembroStaff organizzatore, UUID hackathonId,
                        MembroStaff mentore) {
        // Solo organizzatori possono assegnare mentori
        if (organizzatore.getRuolo() != RuoloStaff.ORGANIZZATORE) {
            throw new IllegalArgumentException("Non autorizzato");
        }

        Hackathon h = repository.findById(hackathonId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Hackathon non trovato: " + hackathonId
                ));

        h.aggiungiMentore(mentore);
        repository.save(h); // aggiorna
    }
}