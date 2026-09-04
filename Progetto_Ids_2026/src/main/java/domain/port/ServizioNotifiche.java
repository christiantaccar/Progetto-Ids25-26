package domain.port;

import domain.models.PersonaRegistrata;

/** Porta verso il canale con cui il sistema avvisa una persona registrata. */
public interface ServizioNotifiche {
    void invia(PersonaRegistrata destinatario, String messaggio);
}
