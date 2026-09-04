package application;

import domain.models.CallProposta;
import domain.models.Hackathon;
import domain.models.MembroStaff;
import domain.models.Team;
import domain.repository.CallPropostaRepository;
import domain.repository.TeamRepository;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Caso d'uso "Propone call al team".
 *
 * Puo' proporre una call solo un mentore assegnato all'hackathon a cui il
 * team e' attualmente iscritto, e solo se non c'e' gia' una sua proposta
 * pendente per quel team.
 */
public class ProponiCallService {

    private final TeamRepository teamRepository;
    private final CallPropostaRepository callPropostaRepository;
    private final GestoreNotificheCall gestoreNotifiche;

    public ProponiCallService(TeamRepository teamRepository,
                              CallPropostaRepository callPropostaRepository,
                              GestoreNotificheCall gestoreNotifiche) {
        this.teamRepository = Objects.requireNonNull(teamRepository);
        this.callPropostaRepository = Objects.requireNonNull(callPropostaRepository);
        this.gestoreNotifiche = Objects.requireNonNull(gestoreNotifiche);
    }

    public CallProposta execute(MembroStaff mentore, UUID teamId, LocalDateTime dataOra, String link) {
        Objects.requireNonNull(mentore, "Mentore obbligatorio");
        Objects.requireNonNull(teamId, "Id team obbligatorio");
        Objects.requireNonNull(dataOra, "Data e ora obbligatorie");
        Objects.requireNonNull(link, "Link obbligatorio");

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team non trovato: " + teamId));

        Hackathon hackathon = team.getHackathonAttuale();
        if (hackathon == null) {
            throw new IllegalStateException("Il team non e' iscritto a nessun hackathon");
        }

        // Deve essere un mentore assegnato a QUESTO hackathon
        if (!hackathon.getMentori().contains(mentore)) {
            throw new IllegalArgumentException(
                    "Non autorizzato: non sei un mentore di questo hackathon");
        }

        // Una sola proposta pendente per coppia mentore-team
        if (callPropostaRepository.findPendentePer(mentore, team).isPresent()) {
            throw new IllegalStateException(
                    "Esiste gia' una proposta di call pendente per questo team");
        }

        CallProposta proposta = new CallProposta(mentore, team, dataOra, link);
        callPropostaRepository.save(proposta);

        // Il service pubblica l'evento e non sa chi lo ascolta (pattern Observer)
        gestoreNotifiche.notificaPropostaCreata(proposta);

        return proposta;
    }
}
