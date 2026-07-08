package application;

import domain.models.Hackathon;
import domain.models.HackathonData;
import domain.models.MembroStaff;
import domain.repository.HackathonRepository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CreateHackathonService {

    private final HackathonRepository repository;

    public CreateHackathonService(HackathonRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public Hackathon execute(MembroStaff organizzatore, HackathonData data, MembroStaff giudice, List<MembroStaff> mentori) {
        // Validazione ruoli
        if (!organizzatore.isOrganizzatore()) {
            throw new IllegalArgumentException("Solo un organizzatore può creare un hackathon");
        }
        if (!giudice.isGiudice()) {
            throw new IllegalArgumentException("Solo un giudice può essere assegnato");
        }
        if (mentori == null || mentori.isEmpty()) {
            throw new IllegalArgumentException("Deve essere assegnato almeno un mentore");
        }
        for (MembroStaff mentore : mentori) {
            if (!mentore.isMentore()) {
                throw new IllegalArgumentException("Solo un mentore può essere assegnato");
            }
        }

        // Creazione tramite Builder (già validato in data)
        Hackathon h = new Hackathon(UUID.randomUUID(), data);
        h.assegnaOrganizzatore(organizzatore);
        h.assegnaGiudice(giudice);
        for (MembroStaff mentore : mentori) {
            h.aggiungiMentore(mentore);
        }

        // Persistenza
        repository.save(h);
        return h;
    }
}