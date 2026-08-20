package domain.models.stato;

import domain.enums.StatoHackathon;

/**
 * Punto unico di traduzione fra l'enum persistito e l'oggetto-stato.
 * Servira' con Spring Boot / JPA: sul database si salva l'enum, in memoria
 * si ricostruisce lo stato concreto corrispondente.
 */
public final class StatiHackathon {

    private StatiHackathon() {
        // classe di sole utility
    }

    public static StatoHackathonState iniziale() {
        return new InIscrizione();
    }

    public static StatoHackathonState da(StatoHackathon tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Stato obbligatorio");
        }
        switch (tipo) {
            case IN_ISCRIZIONE:
                return new InIscrizione();
            case IN_CORSO:
                return new InCorso();
            case IN_VALUTAZIONE:
                return new InValutazione();
            case CONCLUSO:
                return new Concluso();
            default:
                throw new IllegalArgumentException("Stato non riconosciuto: " + tipo);
        }
    }
}
