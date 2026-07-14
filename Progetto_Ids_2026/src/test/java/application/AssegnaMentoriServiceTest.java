package application;

import domain.enums.RuoloStaff;
import domain.models.Hackathon;
import domain.models.HackathonData;
import domain.models.MembroStaff;
import domain.repository.HackathonRepository;
import infrastructure.repository.InMemoryHackathonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AssegnaMentoriServiceTest {

    private HackathonRepository repository;
    private CreateHackathonService createService;
    private AssegnaMentoriService assegnaService;

    private MembroStaff organizzatore;
    private MembroStaff altroOrganizzatore;
    private MembroStaff giudice;
    private MembroStaff mentoreIniziale;
    private MembroStaff nuovoMentore;

    @BeforeEach
    void setUp() {
        repository = new InMemoryHackathonRepository(); // Clock reale di default
        createService = new CreateHackathonService(repository);
        assegnaService = new AssegnaMentoriService(repository);

        organizzatore = new MembroStaff(RuoloStaff.ORGANIZZATORE, "Mario");
        altroOrganizzatore = new MembroStaff(RuoloStaff.ORGANIZZATORE, "Wario");
        giudice = new MembroStaff(RuoloStaff.GIUDICE, "Luigi");
        mentoreIniziale = new MembroStaff(RuoloStaff.MENTORE, "Peach");
        nuovoMentore = new MembroStaff(RuoloStaff.MENTORE, "Toad");
    }

    private HackathonData datiValidi() {
        return HackathonData.builder()
                .nome("HackHub Test")
                .regolamento("Regolamento di prova")
                .luogo("Pesaro")
                .dataInizio(LocalDate.of(2026, 9, 1))
                .dataFine(LocalDate.of(2026, 9, 3))
                .scadenzaIscrizioni(LocalDate.of(2026, 8, 25))
                .premio(500.0)
                .maxTeam(10)
                .build();
    }

    private Hackathon creaHackathon() {
        return createService.execute(organizzatore, datiValidi(), giudice, List.of(mentoreIniziale));
    }

    @Test
    void assegnaNuovoMentoreConSuccesso() {
        Hackathon h = creaHackathon();

        assegnaService.execute(organizzatore, h.getId(), nuovoMentore);

        Hackathon aggiornato = repository.findById(h.getId()).orElseThrow();
        assertTrue(aggiornato.getMentori().contains(nuovoMentore));
        assertEquals(2, aggiornato.getMentori().size());
    }

    @Test
    void rifiutaSeChiamanteNonOrganizzatore() {
        Hackathon h = creaHackathon();

        assertThrows(IllegalArgumentException.class, () ->
                assegnaService.execute(giudice, h.getId(), nuovoMentore));
    }

    @Test
    void rifiutaSeOrganizzatoreNonProprietarioDiQuellHackathon() {
        Hackathon h = creaHackathon();

        assertThrows(IllegalArgumentException.class, () ->
                assegnaService.execute(altroOrganizzatore, h.getId(), nuovoMentore));
    }

    @Test
    void rifiutaSeHackathonNonEsiste() {
        assertThrows(IllegalArgumentException.class, () ->
                assegnaService.execute(organizzatore, UUID.randomUUID(), nuovoMentore));
    }

    @Test
    void rifiutaSeMentoreGiaAssegnato() {
        Hackathon h = creaHackathon();

        assertThrows(IllegalArgumentException.class, () ->
                assegnaService.execute(organizzatore, h.getId(), mentoreIniziale));
    }

    @Test
    void rifiutaSeHackathonConcluso() {
        // Repository con Clock "congelato" al 10 settembre 2026,
        // cioè dopo la dataFine (3 settembre) -> stato IN_VALUTAZIONE
        Clock clockFisso = Clock.fixed(
                LocalDate.of(2026, 9, 10).atStartOfDay(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault()
        );
        HackathonRepository repoConTempoFisso = new InMemoryHackathonRepository(clockFisso);
        CreateHackathonService createConTempoFisso = new CreateHackathonService(repoConTempoFisso);
        AssegnaMentoriService assegnaConTempoFisso = new AssegnaMentoriService(repoConTempoFisso);

        Hackathon h = createConTempoFisso.execute(organizzatore, datiValidi(), giudice, List.of(mentoreIniziale));

        // Non serve più chiamare aggiornaStato() a mano:
        // ogni findById/save attraverso questo repository userà il Clock fisso.

        assertThrows(IllegalStateException.class, () ->
                assegnaConTempoFisso.execute(organizzatore, h.getId(), nuovoMentore));
    }
}