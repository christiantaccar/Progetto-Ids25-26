package application;

import domain.models.Hackathon;
import domain.models.MembroStaff;
import domain.models.Sottomissione;
import domain.models.Team;
import domain.repository.HackathonRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class VisualizzaSottomissioniService {

    private final HackathonRepository hackathonRepository;

    public VisualizzaSottomissioniService(HackathonRepository hackathonRepository) {
        this.hackathonRepository = Objects.requireNonNull(hackathonRepository);
    }

    public static class VoceSottomissione {
        public final Team team;
        public final Sottomissione sottomissione; // null se il team non ha ancora inviato nulla

        public VoceSottomissione(Team team, Sottomissione sottomissione) {
            this.team = team;
            this.sottomissione = sottomissione;
        }
    }

    public List<VoceSottomissione> execute(MembroStaff richiedente, UUID hackathonId) {
        Objects.requireNonNull(richiedente, "Richiedente obbligatorio");
        Objects.requireNonNull(hackathonId, "Id hackathon obbligatorio");

        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon non trovato: " + hackathonId));

        // Autorizzato solo chi è effettivamente assegnato a QUESTO hackathon:
        // l'organizzatore proprietario, il giudice assegnato, o uno dei mentori assegnati.
        boolean autorizzato = richiedente.equals(hackathon.getOrganizzatore())
                || richiedente.equals(hackathon.getGiudice())
                || hackathon.getMentori().contains(richiedente);

        if (!autorizzato) {
            throw new IllegalArgumentException("Non autorizzato: non fai parte dello staff di questo hackathon");
        }

        List<VoceSottomissione> risultato = new ArrayList<>();
        for (Team team : hackathon.getTeamIscritti()) {
            risultato.add(new VoceSottomissione(team, team.getSottomissioneAttuale()));
        }
        return risultato;
    }
}