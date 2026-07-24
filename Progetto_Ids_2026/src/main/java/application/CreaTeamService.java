package application;

import domain.models.Team;
import domain.models.Utente;
import domain.repository.TeamRepository;

import java.util.List;
import java.util.Objects;

public class CreaTeamService {

    private final TeamRepository teamRepository;
    private final InvitaMembriService invitaMembriService;

    public CreaTeamService(TeamRepository teamRepository, InvitaMembriService invitaMembriService) {
        this.teamRepository = Objects.requireNonNull(teamRepository);
        this.invitaMembriService = Objects.requireNonNull(invitaMembriService);
    }

    public static class RisultatoCreazione {
        public final Team team;
        public final List<Utente> esclusi;

        public RisultatoCreazione(Team team, List<Utente> esclusi) {
            this.team = team;
            this.esclusi = esclusi;
        }
    }

    public RisultatoCreazione execute(Utente creatore, String nomeTeam, List<Utente> invitati) {
        Objects.requireNonNull(creatore, "Creatore obbligatorio");

        if (creatore.isInTeam()) {
            throw new IllegalStateException("L'utente è già membro di un team");
        }

        Team team = new Team(nomeTeam, creatore);
        creatore.setTeamAttuale(team);
        teamRepository.save(team);

        InvitaMembriService.RisultatoInviti risultatoInviti = invitaMembriService.execute(team, invitati);

        return new RisultatoCreazione(team, risultatoInviti.esclusi);
    }
}