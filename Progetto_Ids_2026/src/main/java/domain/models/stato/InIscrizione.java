package domain.models.stato;

import domain.enums.StatoHackathon;
import domain.models.HackathonData;

import java.time.LocalDate;

/**
 * Le iscrizioni sono aperte: i team possono iscriversi e l'Organizzatore
 * puo' ancora aggiungere Mentori.
 */
public final class InIscrizione implements StatoHackathonState {

    @Override
    public StatoHackathon tipo() {
        return StatoHackathon.IN_ISCRIZIONE;
    }

    @Override
    public StatoHackathonState prossimo(HackathonData data, LocalDate oggi) {
        if (!oggi.isBefore(data.getDataFine())) {
            return new InValutazione();
        }
        if (!oggi.isBefore(data.getDataInizio())) {
            return new InCorso();
        }
        return this;
    }

    @Override
    public boolean puoIscrivereTeam() {
        return true;
    }

    @Override
    public boolean puoAggiungereMentori() {
        return true;
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
        return o instanceof InIscrizione;
    }

    @Override
    public int hashCode() {
        return InIscrizione.class.hashCode();
    }

    @Override
    public String toString() {
        return tipo().name();
    }
}
