package application.observer;

import domain.models.CallProposta;
import domain.observer.CallPropostaObserver;
import domain.port.ServizioNotifiche;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Avvisa la controparte a ogni passaggio: il Capo Team quando arriva una
 * proposta, il Mentore quando riceve una risposta.
 */
public class NotificaControparteObserver implements CallPropostaObserver {

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ServizioNotifiche notifiche;

    public NotificaControparteObserver(ServizioNotifiche notifiche) {
        this.notifiche = Objects.requireNonNull(notifiche);
    }

    @Override
    public void onPropostaCreata(CallProposta proposta) {
        notifiche.invia(
                proposta.getTeam().getCapoTeam(),
                "Nuova proposta di call in attesa da " + proposta.getMentore().getNome()
                        + " per il " + proposta.getDataOra().format(FORMATO));
    }

    @Override
    public void onPropostaAccettata(CallProposta proposta) {
        notifiche.invia(
                proposta.getMentore(),
                "Il team " + proposta.getTeam().getNome() + " ha accettato la call del "
                        + proposta.getDataOra().format(FORMATO));
    }

    @Override
    public void onPropostaRifiutata(CallProposta proposta) {
        notifiche.invia(
                proposta.getMentore(),
                "Il team " + proposta.getTeam().getNome() + " ha rifiutato la call del "
                        + proposta.getDataOra().format(FORMATO));
    }
}
