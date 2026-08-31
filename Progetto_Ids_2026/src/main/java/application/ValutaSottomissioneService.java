package application;

import domain.models.Hackathon;
import domain.models.MembroStaff;
import domain.models.Sottomissione;
import domain.models.Team;
import domain.repository.TeamRepository;

import java.util.Objects;
import java.util.UUID;

public class ValutaSottomissioneService {

    private final TeamRepository teamRepository;

    public ValutaSottomissioneService(TeamRepository teamRepository) {
        this.teamRepository = Objects.requireNonNull(teamRepository);
    }

    public Sottomissione execute(MembroStaff giudice, UUID teamId, int punteggio) {
        Objects.requireNonNull(giudice, "Giudice obbligatorio");
        Objects.requireNonNull(teamId, "Id team obbligatorio");

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team non trovato: " + teamId));

        Hackathon hackathon = team.getHackathonAttuale();
        if (hackathon == null) {
            throw new IllegalStateException("Il team non è iscritto a nessun hackathon");
        }

        // Deve essere il Giudice assegnato a QUESTO hackathon
        if (!giudice.equals(hackathon.getGiudice())) {
            throw new IllegalArgumentException("Non autorizzato: non sei il giudice di questo hackathon");
        }

        // La decisione è delegata allo stato corrente dell'hackathon (pattern State)
        if (!hackathon.puoValutareSottomissioni()) {
            throw new IllegalStateException(
                    "Non è possibile valutare sottomissioni per un hackathon in stato " + hackathon.getStato());
        }

        Sottomissione sottomissione = team.getSottomissioneAttuale();
        if (sottomissione == null) {
            throw new IllegalStateException("Il team non ha ancora inviato nessuna sottomissione");
        }

        // Il voto è definitivo: Sottomissione.valuta(...) rifiuta da sola una seconda valutazione
        sottomissione.valuta(punteggio);
        teamRepository.save(team);

        return sottomissione;
    }
}