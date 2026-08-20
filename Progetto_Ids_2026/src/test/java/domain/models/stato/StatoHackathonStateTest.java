package domain.models.stato;

import domain.enums.StatoHackathon;
import domain.models.HackathonData;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class StatoHackathonStateTest {

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

    // ====== PERMESSI ======

    @Test
    void inIscrizioneConsenteIscrizioniEMentori() {
        StatoHackathonState s = new InIscrizione();
        assertTrue(s.puoIscrivereTeam());
        assertTrue(s.puoAggiungereMentori());
        assertFalse(s.puoRicevereSottomissioni());
        assertFalse(s.puoValutareSottomissioni());
        assertFalse(s.puoProclamareVincitore());
    }

    @Test
    void inCorsoChiudeIscrizioniEApreSottomissioni() {
        StatoHackathonState s = new InCorso();
        assertFalse(s.puoIscrivereTeam());
        assertTrue(s.puoAggiungereMentori());
        assertTrue(s.puoRicevereSottomissioni());
        assertFalse(s.puoValutareSottomissioni());
        assertFalse(s.puoProclamareVincitore());
    }

    @Test
    void inValutazioneConsenteSoloValutazioneEProclamazione() {
        StatoHackathonState s = new InValutazione();
        assertFalse(s.puoIscrivereTeam());
        assertFalse(s.puoAggiungereMentori());
        assertFalse(s.puoRicevereSottomissioni());
        assertTrue(s.puoValutareSottomissioni());
        assertTrue(s.puoProclamareVincitore());
    }

    @Test
    void conclusoNonConsenteNulla() {
        StatoHackathonState s = new Concluso();
        assertFalse(s.puoIscrivereTeam());
        assertFalse(s.puoAggiungereMentori());
        assertFalse(s.puoRicevereSottomissioni());
        assertFalse(s.puoValutareSottomissioni());
        assertFalse(s.puoProclamareVincitore());
    }

    // ====== TRANSIZIONI ======

    @Test
    void inIscrizioneRestaTalePrimaDellInizio() {
        StatoHackathonState s = new InIscrizione().prossimo(datiValidi(), LocalDate.of(2026, 8, 30));
        assertEquals(StatoHackathon.IN_ISCRIZIONE, s.tipo());
    }

    @Test
    void inIscrizionePassaAInCorsoAllaDataInizio() {
        StatoHackathonState s = new InIscrizione().prossimo(datiValidi(), LocalDate.of(2026, 9, 1));
        assertEquals(StatoHackathon.IN_CORSO, s.tipo());
    }

    @Test
    void inIscrizioneSaltaDirettamenteAInValutazioneSeGiaFinito() {
        StatoHackathonState s = new InIscrizione().prossimo(datiValidi(), LocalDate.of(2026, 9, 5));
        assertEquals(StatoHackathon.IN_VALUTAZIONE, s.tipo());
    }

    @Test
    void inCorsoPassaAInValutazioneAllaDataFine() {
        StatoHackathonState s = new InCorso().prossimo(datiValidi(), LocalDate.of(2026, 9, 3));
        assertEquals(StatoHackathon.IN_VALUTAZIONE, s.tipo());
    }

    @Test
    void inCorsoNonTornaIndietro() {
        StatoHackathonState s = new InCorso().prossimo(datiValidi(), LocalDate.of(2026, 8, 1));
        assertEquals(StatoHackathon.IN_CORSO, s.tipo());
    }

    @Test
    void inValutazioneNonHaTransizioniAutomatiche() {
        StatoHackathonState s = new InValutazione().prossimo(datiValidi(), LocalDate.of(2030, 1, 1));
        assertEquals(StatoHackathon.IN_VALUTAZIONE, s.tipo());
    }

    @Test
    void conclusoEStatoFinale() {
        StatoHackathonState s = new Concluso().prossimo(datiValidi(), LocalDate.of(2030, 1, 1));
        assertEquals(StatoHackathon.CONCLUSO, s.tipo());
    }

    // ====== FACTORY ======

    @Test
    void factoryRicostruisceLoStatoDallEnum() {
        assertEquals(StatoHackathon.IN_ISCRIZIONE, StatiHackathon.da(StatoHackathon.IN_ISCRIZIONE).tipo());
        assertEquals(StatoHackathon.IN_CORSO, StatiHackathon.da(StatoHackathon.IN_CORSO).tipo());
        assertEquals(StatoHackathon.IN_VALUTAZIONE, StatiHackathon.da(StatoHackathon.IN_VALUTAZIONE).tipo());
        assertEquals(StatoHackathon.CONCLUSO, StatiHackathon.da(StatoHackathon.CONCLUSO).tipo());
    }

    @Test
    void factoryRifiutaStatoNullo() {
        assertThrows(IllegalArgumentException.class, () -> StatiHackathon.da(null));
    }

    @Test
    void statoInizialeEInIscrizione() {
        assertEquals(StatoHackathon.IN_ISCRIZIONE, StatiHackathon.iniziale().tipo());
    }
}
