package domain.models;

import domain.enums.RuoloStaff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SottomissioneTest {

    private Team team;
    private Hackathon hackathon;

    private HackathonData datiValidi() {
        return HackathonData.builder()
                .nome("Test")
                .regolamento("Reg")
                .luogo("Pesaro")
                .dataInizio(LocalDateTime.of(2026, 9, 1,0,0))
                .dataFine(LocalDateTime.of(2026, 9, 3,0,0))
                .scadenzaIscrizioni(LocalDate.of(2026, 8, 25))
                .premio(0)
                .maxTeam(5)
                .build();
    }

    @BeforeEach
    void setUp() {
        Utente capo = new Utente("Mario", "mario@test.it");
        team = new Team("I Fantastici", capo);
        hackathon = new Hackathon(UUID.randomUUID(), datiValidi());
    }

    @Test
    void creaSottomissioneValida() {
        Sottomissione s = new Sottomissione(team, hackathon, "https://github.com/team/progetto");

        assertEquals(team, s.getTeam());
        assertEquals(hackathon, s.getHackathon());
        assertEquals("https://github.com/team/progetto", s.getLink());
        assertFalse(s.isValutata());
    }

    @Test
    void rifiutaLinkVuoto() {
        assertThrows(IllegalArgumentException.class, () ->
                new Sottomissione(team, hackathon, "   "));
    }

    @Test
    void rifiutaLinkNullo() {
        assertThrows(NullPointerException.class, () ->
                new Sottomissione(team, hackathon, null));
    }

    @Test
    void valutaConPunteggioValido() {
        Sottomissione s = new Sottomissione(team, hackathon, "https://github.com/team/progetto");

        s.valuta(85);

        assertTrue(s.isValutata());
        assertEquals(85, s.getPunteggio());
    }

    @Test
    void rifiutaPunteggioFuoriRange() {
        Sottomissione s = new Sottomissione(team, hackathon, "https://github.com/team/progetto");

        assertThrows(IllegalArgumentException.class, () -> s.valuta(-1));
        assertThrows(IllegalArgumentException.class, () -> s.valuta(101));
    }

    @Test
    void rifiutaRivalutazione() {
        Sottomissione s = new Sottomissione(team, hackathon, "https://github.com/team/progetto");
        s.valuta(70);

        assertThrows(IllegalStateException.class, () -> s.valuta(90));
        assertEquals(70, s.getPunteggio()); // il voto resta quello originale
    }
}