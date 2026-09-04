package infrastructure.notifica;

import domain.models.PersonaRegistrata;
import domain.port.ServizioNotifiche;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione di prova: registra le notifiche in memoria invece di
 * inviarle davvero, cosi' i test possono verificare che siano partite.
 */
public class NotificheInMemory implements ServizioNotifiche {

    public static class Notifica {
        public final PersonaRegistrata destinatario;
        public final String messaggio;

        public Notifica(PersonaRegistrata destinatario, String messaggio) {
            this.destinatario = destinatario;
            this.messaggio = messaggio;
        }
    }

    private final List<Notifica> inviate = new ArrayList<>();

    @Override
    public void invia(PersonaRegistrata destinatario, String messaggio) {
        inviate.add(new Notifica(destinatario, messaggio));
    }

    public List<Notifica> getInviate() {
        return List.copyOf(inviate);
    }

    public void svuota() {
        inviate.clear();
    }
}
