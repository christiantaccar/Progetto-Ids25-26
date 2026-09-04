package domain.port;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Porta verso il servizio di calendario esterno.
 *
 * La firma usa solo tipi primitivi: un servizio esterno non conosce le
 * classi di dominio. L'identificativo restituito e' assegnato dal servizio
 * ed e' opaco per noi.
 */
public interface CalendarioService {

    String aggiungiEvento(String titolo,
                          LocalDateTime dataOra,
                          List<String> emailPartecipanti,
                          String link);

    void rimuoviEvento(String idEventoEsterno);
}
