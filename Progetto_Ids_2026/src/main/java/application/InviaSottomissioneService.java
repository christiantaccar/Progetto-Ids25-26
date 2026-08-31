package application;

import domain.models.Hackathon;
import domain.models.Sottomissione;
import domain.models.Team;
import domain.models.Utente;
import domain.repository.SottomissioneRepository;
import domain.repository.TeamRepository;

import java.util.Objects;
import java.util.UUID;

public class InviaSottomissioneService {

    private final TeamRepository teamRepository;
    private final SottomissioneRepository sottomissioneRepository;

    public InviaSottomissioneService(TeamRepository teamRepository, SottomissioneRepository sottomissioneRepository) {
        this.teamRepository = Objects.requireNonNull(teamRepository);
        this.sottomissioneRepository = Objects.requireNonNull(sottomissioneRepository);
    }

    public Sottomissione execute(Utente richiedente, UUID teamId, String link) {
        Objects.requireNonNull(richiedente, "Richiedente obbligatorio");
        Objects.requireNonNull(teamId, "Id team obbligatorio");

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team non trovato: " + teamId));

        if (!richiedente.equals(team.getCapoTeam())) {
            throw new IllegalArgumentException("Solo il Capo Team può inviare la sottomissione");
        }

        Hackathon hackathon = team.getHackathonAttuale();
        if (hackathon == null) {
            throw new IllegalStateException("Il team non è iscritto a nessun hackathon");
        }

        // La decisione è delegata allo stato corrente dell'hackathon (pattern State)
        if (!hackathon.puoRicevereSottomissioni()) {
            throw new IllegalStateException(
                    "Non è possibile inviare sottomissioni per un hackathon in stato " + hackathon.getStato());
        }

        // Ogni invio crea una nuova Sottomissione e sostituisce la precedente,
        // senza controlli: finché l'hackathon è IN_CORSO si può ripetere quante volte si vuole.
        Sottomissione sottomissione = new Sottomissione(team, hackathon, link);
        team.setSottomissioneAttuale(sottomissione);

        sottomissioneRepository.save(sottomissione);
        teamRepository.save(team);

        return sottomissione;
    }
}