package application;

import domain.enums.StatoHackathon;
import domain.models.Hackathon;
import domain.models.Team;
import domain.models.Utente;
import domain.repository.TeamRepository;

import java.util.Objects;

/**
 * UC "Espellere componente".
 *
 * Solo il capo team può espellere un membro (diverso da se stesso).
 * Come per "Lasciare team", l'operazione non è consentita se la squadra
 * è iscritta a un hackathon la cui gara è già iniziata.
 */
public class EspellereComponenteService {

    private final TeamRepository teamRepository;

    public EspellereComponenteService(TeamRepository teamRepository) {
        this.teamRepository = Objects.requireNonNull(teamRepository, "TeamRepository obbligatorio");
    }

    public void execute(Utente capo, Team team, Utente componente) {
        Objects.requireNonNull(capo, "Capo team obbligatorio");
        Objects.requireNonNull(team, "Team obbligatorio");
        Objects.requireNonNull(componente, "Componente da espellere obbligatorio");

        if (!capo.equals(team.getCapoTeam())) {
            throw new IllegalArgumentException("Solo il capo team può espellere componenti");
        }
        if (componente.equals(capo)) {
            throw new IllegalArgumentException("Il capo team non può espellere se stesso");
        }
        if (!team.getMembri().contains(componente)) {
            throw new IllegalArgumentException("L'utente non è membro di questo team");
        }

        Hackathon hackathon = team.getHackathonAttuale();
        if (hackathon != null && hackathon.getStato() != StatoHackathon.IN_ISCRIZIONE) {
            throw new IllegalStateException(
                    "Non è possibile modificare la squadra: la gara è già iniziata");
        }

        team.rimuoviMembro(componente);
        componente.setTeamAttuale(null);
        teamRepository.save(team);
    }
}
