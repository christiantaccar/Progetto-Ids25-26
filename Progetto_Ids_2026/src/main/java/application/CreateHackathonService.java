package application;

import domain.models.Hackathon;
import domain.models.HackathonData;
import domain.models.MembroStaff;
import domain.repository.HackathonRepository;
import domain.factory.HackathonFactory;

import java.util.Objects;

public class CreateHackathonService {

    private final HackathonFactory factory;
    private final HackathonRepository repository;

    public CreateHackathonService(HackathonFactory factory,
                                  HackathonRepository repository) {
        this.factory = Objects.requireNonNull(factory);
        this.repository = Objects.requireNonNull(repository);
    }

    public Hackathon execute(MembroStaff organizzatore, HackathonData data, MembroStaff giudice,MembroStaff mentore) {
        // Validazione ruolo
        if (!organizzatore.isOrganizzatore()) {
            throw new IllegalArgumentException(
                    "Solo un organizzatore può creare un hackathon"
            );
        }

        if (!giudice.isGiudice()) {
            throw new IllegalArgumentException(
                    "Solo un giudice può essere assegnato"
            );
        }
        if (!mentore.isMentore()) {
            throw new IllegalArgumentException(
                    "Solo un mentore può essere assegnato"
            );
        }
        // Creazione tramite Factory (pattern!)
        Hackathon h = factory.create(data);
        h.assegnaOrganizzatore(organizzatore);
        h.assegnaGiudice(giudice);
        h.aggiungiMentore(mentore);


        // Persistenza
        repository.save(h);
        return h;
    }
}