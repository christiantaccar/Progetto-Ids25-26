package domain.models.stato;

import domain.enums.StatoHackathon;
import domain.models.HackathonData;

import java.time.LocalDate;

/**
 * L'hackathon e' terminato: il Giudice valuta le sottomissioni.
 * Quando tutte sono state valutate, l'Organizzatore puo' proclamare il vincitore
 * (transizione manuale verso CONCLUSO, non guidata dal tempo).
 */
public final class InValutazione implements StatoHackathonState {

    @Override
    public StatoHackathon tipo() {
        return StatoHackathon.IN_VALUTAZIONE;
    }

    @Override
    public StatoHackathonState prossimo(HackathonData data, LocalDate oggi) {
        // Nessuna transizione automatica: si esce da questo stato solo
        // con la proclamazione del vincitore da parte dell'Organizzatore.
        return this;
    }

    @Override
    public boolean puoIscrivereTeam() {
        return false;
    }

    @Override
    public boolean puoAggiungereMentori() {
        return false;
    }

    @Override
    public boolean puoRicevereSottomissioni() {
        return false;
    }

    @Override
    public boolean puoValutareSottomissioni() {
        return true;
    }

    @Override
    public boolean puoProclamareVincitore() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof InValutazione;
    }

    @Override
    public int hashCode() {
        return InValutazione.class.hashCode();
    }

    @Override
    public String toString() {
        return tipo().name();
    }
}
