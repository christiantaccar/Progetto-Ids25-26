package domain.models;

import domain.enums.StatoHackathon;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HackathonStatoTest {

    private HackathonData datiValidi() {
        return HackathonData.builder()
                .nome("Test")
                .regolamento("Reg")
                .luogo("Pesaro")
                .dataInizio(LocalDate.of(2026, 9, 1))
                .dataFine(LocalDate.of(2026, 9, 3))
                .scadenzaIscrizioni(LocalDate.of(2026, 8, 25))
                .premio(0)
                .maxTeam(5)
                .build();
    }

    @Test
    void primaDellInizioRestaInIscrizione() {
        Hackathon h = new Hackathon(java.util.UUID.randomUUID(), datiValidi());
        h.aggiornaStato(LocalDate.of(2026, 8, 30));
        assertEquals(StatoHackathon.IN_ISCRIZIONE, h.getStato());
    }

    @Test
    void dopoScadenzaIscrizioniMaPrimaInizioRestaInIscrizione() {
        Hackathon h = new Hackathon(java.util.UUID.randomUUID(), datiValidi());
        h.aggiornaStato(LocalDate.of(2026, 8, 31)); // dopo scadenza (25/8), prima inizio (1/9)
        assertEquals(StatoHackathon.IN_ISCRIZIONE, h.getStato());
    }

    @Test
    void durantEventoDiventaInCorso() {
        Hackathon h = new Hackathon(java.util.UUID.randomUUID(), datiValidi());
        h.aggiornaStato(LocalDate.of(2026, 9, 2));
        assertEquals(StatoHackathon.IN_CORSO, h.getStato());
    }

    @Test
    void dopoFineDiventaInValutazione() {
        Hackathon h = new Hackathon(java.util.UUID.randomUUID(), datiValidi());
        h.aggiornaStato(LocalDate.of(2026, 9, 5));
        assertEquals(StatoHackathon.IN_VALUTAZIONE, h.getStato());
    }
}