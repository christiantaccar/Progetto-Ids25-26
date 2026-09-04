package application.observer;

import domain.models.CallProposta;
import domain.models.Utente;
import domain.observer.CallPropostaObserver;
import domain.port.CalendarioService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Riporta sul calendario esterno le call accettate.
 *
 * Costruisce titolo e partecipanti a partire dal dominio e passa al servizio
 * esterno solo tipi primitivi.
 */
public class CalendarioObserver implements CallPropostaObserver {

    private final CalendarioService calendario;

    public CalendarioObserver(CalendarioService calendario) {
        this.calendario = Objects.requireNonNull(calendario);
    }

    @Override
    public void onPropostaAccettata(CallProposta proposta) {
        String titolo = "Call con il mentore " + proposta.getMentore().getNome()
                + " - team " + proposta.getTeam().getNome();

        List<String> partecipanti = new ArrayList<>();
        for (Utente membro : proposta.getTeam().getTuttiIMembri()) {
            partecipanti.add(membro.getEmail());
        }

        calendario.aggiungiEvento(titolo, proposta.getDataOra(), partecipanti, proposta.getLink());
    }
}
