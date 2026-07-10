package application;

import domain.enums.RuoloStaff;
import domain.enums.StatoHackathon;
import domain.models.Hackathon;
import domain.models.MembroStaff;
import domain.repository.HackathonRepository;

import java.util.Objects;
import java.util.UUID;

public class AssegnaMentoriService {

    private final HackathonRepository repository;

    public AssegnaMentoriService(HackathonRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public void execute(MembroStaff organizzatore, UUID hackathonId, MembroStaff mentore) {
        Objects.requireNonNull(organizzatore, "Organizzatore obbligatorio");
        Objects.requireNonNull(hackathonId, "Id hackathon obbligatorio");
        Objects.requireNonNull(mentore, "Mentore obbligatorio");

        // Solo organizzatori possono assegnare mentori
        if (organizzatore.getRuolo() != RuoloStaff.ORGANIZZATORE) {
            throw new IllegalArgumentException("Non autorizzato: solo un organizzatore può assegnare mentori");
        }

        Hackathon h = repository.findById(hackathonId)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon non trovato: " + hackathonId));

        // Deve essere l'organizzatore proprietario di QUESTO hackathon
        if (!organizzatore.equals(h.getOrganizzatore())) {
            throw new IllegalArgumentException("Non autorizzato: non sei l'organizzatore di questo hackathon");
        }

        // Non si possono aggiungere mentori a un hackathon già in valutazione o concluso
        StatoHackathon stato = h.getStato();
        if (stato == StatoHackathon.IN_VALUTAZIONE || stato == StatoHackathon.CONCLUSO) {
            throw new IllegalStateException("Non è possibile assegnare mentori a un hackathon in stato " + stato);
        }

        // Evita di assegnare due volte lo stesso mentore
        if (h.getMentori().contains(mentore)) {
            throw new IllegalArgumentException("Il mentore è già assegnato a questo hackathon");
        }

        h.aggiungiMentore(mentore);
        repository.save(h);
    }
}