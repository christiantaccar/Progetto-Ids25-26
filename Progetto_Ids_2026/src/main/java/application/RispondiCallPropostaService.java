package application;

import domain.models.CallProposta;
import domain.models.Team;
import domain.models.Utente;
import domain.repository.CallPropostaRepository;

import java.util.Objects;
import java.util.UUID;

/**
 * Caso d'uso "Rispondere a una proposta di call".
 *
 * Solo il Capo Team del team destinatario puo' accettare o rifiutare, e solo
 * finche' la proposta e' pendente.
 */
public class RispondiCallPropostaService {

    private final CallPropostaRepository callPropostaRepository;
    private final GestoreNotificheCall gestoreNotifiche;

    public RispondiCallPropostaService(CallPropostaRepository callPropostaRepository,
                                       GestoreNotificheCall gestoreNotifiche) {
        this.callPropostaRepository = Objects.requireNonNull(callPropostaRepository);
        this.gestoreNotifiche = Objects.requireNonNull(gestoreNotifiche);
    }

    public CallProposta execute(Utente capoTeam, UUID propostaId, boolean accetta) {
        Objects.requireNonNull(capoTeam, "Capo team obbligatorio");
        Objects.requireNonNull(propostaId, "Id proposta obbligatorio");

        CallProposta proposta = callPropostaRepository.findById(propostaId)
                .orElseThrow(() -> new IllegalArgumentException("Proposta non trovata: " + propostaId));

        Team team = proposta.getTeam();
        if (!capoTeam.equals(team.getCapoTeam())) {
            throw new IllegalArgumentException(
                    "Non autorizzato: solo il Capo Team puo' rispondere alla proposta");
        }

        if (!proposta.isPendente()) {
            throw new IllegalStateException("La proposta non e' piu' pendente");
        }

        if (accetta) {
            proposta.accetta();
            callPropostaRepository.save(proposta);
            gestoreNotifiche.notificaPropostaAccettata(proposta);
        } else {
            proposta.rifiuta();
            callPropostaRepository.save(proposta);
            gestoreNotifiche.notificaPropostaRifiutata(proposta);
        }

        return proposta;
    }
}
