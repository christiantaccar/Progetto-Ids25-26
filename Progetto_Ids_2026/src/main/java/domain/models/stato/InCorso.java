package domain.models.stato;

import domain.enums.StatoHackathon;
import domain.models.HackathonData;

import java.time.LocalDate;

/**
 * L'hackathon e' iniziato: le iscrizioni sono chiuse, i team lavorano e
 * possono inviare/aggiornare la propria sottomissione.
 */
public final class InCorso implements StatoHackathonState {

    @Override
    public StatoHackathon tipo() {
        return StatoHackathon.IN_CORSO;
    }

    @Override
    public StatoHackathonState prossimo(HackathonData data, LocalDate oggi) {
        if (!oggi.isBefore(data.getDataFine())) {
            return new InValutazione();
        }
        return this;
    }

    @Override
    public boolean puoIscrivereTeam() {
        return false;
    }

    @Override
    public boolean puoAggiungereMentori() {
        return true;
    }

    @Override
    public boolean puoRicevereSottomissioni() {
        return true;
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
        return o instanceof InCorso;
    }

    @Override
    public int hashCode() {
        return InCorso.class.hashCode();
    }

    @Override
    public String toString() {
        return tipo().name();
    }
}
