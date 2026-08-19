package application;

import domain.models.Team;
import domain.models.Utente;
import domain.repository.TeamRepository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class InvitaAltriUtentiService {

    private final TeamRepository teamRepository;
    private final InvitaMembriService invitaMembriService;

    public InvitaAltriUtentiService(TeamRepository teamRepository, InvitaMembriService invitaMembriService) {
        this.teamRepository = Objects.requireNonNull(teamRepository);
        this.invitaMembriService = Objects.requireNonNull(invitaMembriService);
    }

    public InvitaMembriService.RisultatoInviti execute(Utente richiedente, UUID teamId, List<Utente> invitati) {
        Objects.requireNonNull(richiedente, "Richiedente obbligatorio");
        Objects.requireNonNull(teamId, "Id team obbligatorio");

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team non trovato: " + teamId));

        if (!richiedente.equals(team.getCapoTeam())) {
            throw new IllegalArgumentException("Solo il Capo Team può invitare nuovi membri");
        }

        return invitaMembriService.execute(team, invitati);
    }
}
