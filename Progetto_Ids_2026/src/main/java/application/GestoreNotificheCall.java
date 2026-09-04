package application;

import domain.models.CallProposta;
import domain.observer.CallPropostaObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Design pattern OBSERVER: e' il Subject.
 *
 * I service pubblicano qui gli eventi sulle proposte di call e non sanno chi
 * li ascolta: aggiungere un nuovo osservatore non richiede di modificarli (OCP).
 *
 * Un osservatore che fallisce non deve impedire agli altri di essere avvisati,
 * ne' far fallire l'operazione applicativa che ha generato l'evento: per questo
 * le eccezioni degli osservatori vengono contenute qui.
 */
public class GestoreNotificheCall {

    private final List<CallPropostaObserver> osservatori = new ArrayList<>();

    public void registra(CallPropostaObserver osservatore) {
        Objects.requireNonNull(osservatore, "Osservatore obbligatorio");
        if (!osservatori.contains(osservatore)) {
            osservatori.add(osservatore);
        }
    }

    public void rimuovi(CallPropostaObserver osservatore) {
        osservatori.remove(osservatore);
    }

    public void notificaPropostaCreata(CallProposta proposta) {
        for (CallPropostaObserver o : osservatori) {
            eseguiInSicurezza(() -> o.onPropostaCreata(proposta));
        }
    }

    public void notificaPropostaAccettata(CallProposta proposta) {
        for (CallPropostaObserver o : osservatori) {
            eseguiInSicurezza(() -> o.onPropostaAccettata(proposta));
        }
    }

    public void notificaPropostaRifiutata(CallProposta proposta) {
        for (CallPropostaObserver o : osservatori) {
            eseguiInSicurezza(() -> o.onPropostaRifiutata(proposta));
        }
    }

    private void eseguiInSicurezza(Runnable notifica) {
        try {
            notifica.run();
        } catch (RuntimeException e) {
            // Un osservatore che fallisce non deve fermare la catena.
            System.err.println("Notifica non riuscita: " + e.getMessage());
        }
    }
}
