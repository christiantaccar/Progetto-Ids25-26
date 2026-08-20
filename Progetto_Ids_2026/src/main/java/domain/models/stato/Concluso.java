package domain.models.stato;

import domain.enums.StatoHackathon;
import domain.models.HackathonData;

import java.time.LocalDate;

/**
 * Stato finale: il vincitore e' stato proclamato. Nessuna operazione consentita
 * e nessuna transizione ulteriore.
 */
public final class Concluso implements StatoHackathonState {

    @Override
    public StatoHackathon tipo() {
        return StatoHackathon.CONCLUSO;
    }

    @Override
    public StatoHackathonState prossimo(HackathonData data, LocalDate oggi) {
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
        return false;
    }

    @Override
    public boolean puoProclamareVincitore() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Concluso;
    }

    @Override
    public int hashCode() {
        return Concluso.class.hashCode();
    }

    @Override
    public String toString() {
        return tipo().name();
    }
}
