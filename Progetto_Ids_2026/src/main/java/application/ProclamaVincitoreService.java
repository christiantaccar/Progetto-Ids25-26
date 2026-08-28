package application;

import domain.models.Hackathon;
import domain.models.Sottomissione;
import domain.models.Team;
import domain.repository.HackathonRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ProclamaVincitoreService {

    private final HackathonRepository hackathonRepository;

    public ProclamaVincitoreService(HackathonRepository hackathonRepository) {
        this.hackathonRepository = Objects.requireNonNull(hackathonRepository);
    }

    public static class RisultatoProclamazione {
        public final Team vincitore;                 // null se richiedeSceltaGiudice == true
        public final boolean richiedeSceltaGiudice;
        public final List<Team> candidatiInParita;    // vuoto se non c'è pareggio

        public RisultatoProclamazione(Team vincitore, boolean richiedeSceltaGiudice, List<Team> candidatiInParita) {
            this.vincitore = vincitore;
            this.richiedeSceltaGiudice = richiedeSceltaGiudice;
            this.candidatiInParita = candidatiInParita;
        }
    }

    public RisultatoProclamazione execute(UUID hackathonId, Team sceltaGiudiceInCasoDiParita) {
        Objects.requireNonNull(hackathonId, "Id hackathon obbligatorio");

        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon non trovato: " + hackathonId));

        List<Team> teamIscritti = hackathon.getTeamIscritti();

        if (teamIscritti.isEmpty()) {
            throw new IllegalStateException("Nessun team iscritto: impossibile proclamare un vincitore");
        }

        int punteggioMassimo = -1;
        List<Team> candidati = new ArrayList<>();

        for (Team team : teamIscritti) {
            Sottomissione sottomissione = team.getSottomissioneAttuale();
            if (sottomissione == null || !sottomissione.isValutata()) {
                throw new IllegalStateException(
                        "Non tutti i team hanno una sottomissione valutata: " + team.getNome());
            }

            int punteggio = sottomissione.getPunteggio();
            if (punteggio > punteggioMassimo) {
                punteggioMassimo = punteggio;
                candidati.clear();
                candidati.add(team);
            } else if (punteggio == punteggioMassimo) {
                candidati.add(team);
            }
        }

        if (candidati.size() == 1) {
            return new RisultatoProclamazione(candidati.get(0), false, List.of());
        }

        // Pareggio tra più team: serve la scelta esplicita del Giudice
        if (sceltaGiudiceInCasoDiParita == null) {
            return new RisultatoProclamazione(null, true, List.copyOf(candidati));
        }

        if (!candidati.contains(sceltaGiudiceInCasoDiParita)) {
            throw new IllegalArgumentException("Il team scelto non è tra i candidati in parità");
        }

        return new RisultatoProclamazione(sceltaGiudiceInCasoDiParita, false, List.of());
    }
}