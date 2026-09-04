package infrastructure.calendar;

import domain.port.CalendarioService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Simula un servizio di calendario esterno.
 *
 * Si comporta come un sistema di terze parti: assegna identificativi propri e
 * opachi e non conosce le classi di dominio, che infatti non compaiono nella
 * firma dei suoi metodi. Per semplicita' si assume che il servizio sia sempre
 * raggiungibile.
 */
public class CalendarioSimulato implements CalendarioService {

    private final Map<String, EventoEsterno> eventi = new HashMap<>();

    public static class EventoEsterno {
        public final String titolo;
        public final LocalDateTime dataOra;
        public final List<String> partecipanti;
        public final String link;

        EventoEsterno(String titolo, LocalDateTime dataOra, List<String> partecipanti, String link) {
            this.titolo = titolo;
            this.dataOra = dataOra;
            this.partecipanti = partecipanti;
            this.link = link;
        }
    }

    @Override
    public String aggiungiEvento(String titolo, LocalDateTime dataOra,
                                 List<String> emailPartecipanti, String link) {
        String idEventoEsterno = "EXT-" + UUID.randomUUID();
        eventi.put(idEventoEsterno,
                new EventoEsterno(titolo, dataOra, new ArrayList<>(emailPartecipanti), link));
        return idEventoEsterno;
    }

    @Override
    public void rimuoviEvento(String idEventoEsterno) {
        eventi.remove(idEventoEsterno);
    }

    public int numeroEventi() {
        return eventi.size();
    }

    public EventoEsterno getEvento(String idEventoEsterno) {
        return eventi.get(idEventoEsterno);
    }
}
