package application;

import domain.enums.RuoloStaff;
import domain.models.*;
import domain.repository.HackathonRepository;
import infrastructure.repository.InMemoryHackathonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VisualizzaSottomissioniServiceTest {

    private HackathonRepository hackathonRepository;
    private CreateHackathonService createHackathonService;
    private VisualizzaSottomissioniService visualizzaSottomissioniService;

    private MembroStaff organizzatore;
    private MembroStaff giudice;
    private MembroStaff mentore;
    private MembroStaff estraneo; // membro staff non assegnato a questo hackathon

    @BeforeEach
    void setUp() {
        hackathonRepository = new InMemoryHackathonRepository();
        createHackathonService = new CreateHackathonService(hackathonRepository);
        visualizzaSottomissioniService = new VisualizzaSottomissioniService(hackathonRepository);

        organizzatore = new MembroStaff(RuoloStaff.ORGANIZZATORE, "Mario", "mario@staff.test");
        giudice = new MembroStaff(RuoloStaff.GIUDICE, "Luigi", "luigi@staff.test");
        mentore = new MembroStaff(RuoloStaff.MENTORE, "Peach", "peach@staff.test");
        estraneo = new MembroStaff(RuoloStaff.MENTORE, "Toad", "toad@staff.test");
    }

    private HackathonData datiValidi() {
        return HackathonData.builder()
                .nome("HackHub Test")
                .regolamento("Regolamento di prova")
                .luogo("Pesaro")
                .dataInizio(LocalDateTime.of(2026, 9, 1,0,0))
                .dataFine(LocalDate.of(2026, 9, 3))
                .scadenzaIscrizioni(LocalDate.of(2026, 8, 25))
                .premio(500.0)
                .maxTeam(10)
                .build();
    }

    private Hackathon creaHackathon() {
        return createHackathonService.execute(organizzatore, datiValidi(), giudice, List.of(mentore));
    }

    private Team creaEIscriviTeam(Hackathon hackathon, String nome) {
        Utente capo = new Utente(nome, nome.toLowerCase() + "@test.it");
        Team team = new Team(nome, capo);
        capo.setTeamAttuale(team);
        hackathon.iscriviTeam(team);
        team.setHackathonAttuale(hackathon);
        return team;
    }

    @Test
    void organizzatoreVedeLElencoConSottomissioniPresentiEMancanti() {
        Hackathon h = creaHackathon();
        Team teamA = creaEIscriviTeam(h, "TeamA");
        Team teamB = creaEIscriviTeam(h, "TeamB"); // nessuna sottomissione

        Sottomissione s = new Sottomissione(teamA, h, "https://github.com/TeamA/progetto");
        teamA.setSottomissioneAttuale(s);

        List<VisualizzaSottomissioniService.VoceSottomissione> elenco =
                visualizzaSottomissioniService.execute(organizzatore, h.getId());

        assertEquals(2, elenco.size());
        VisualizzaSottomissioniService.VoceSottomissione voceA = elenco.stream()
                .filter(v -> v.team.equals(teamA)).findFirst().orElseThrow();
        VisualizzaSottomissioniService.VoceSottomissione voceB = elenco.stream()
                .filter(v -> v.team.equals(teamB)).findFirst().orElseThrow();

        assertEquals(s, voceA.sottomissione);
        assertNull(voceB.sottomissione);
    }

    @Test
    void giudiceAssegnatoPuoConsultareLElenco() {
        Hackathon h = creaHackathon();
        creaEIscriviTeam(h, "TeamA");

        List<VisualizzaSottomissioniService.VoceSottomissione> elenco =
                visualizzaSottomissioniService.execute(giudice, h.getId());

        assertEquals(1, elenco.size());
    }

    @Test
    void mentoreAssegnatoPuoConsultareLElenco() {
        Hackathon h = creaHackathon();
        creaEIscriviTeam(h, "TeamA");

        List<VisualizzaSottomissioniService.VoceSottomissione> elenco =
                visualizzaSottomissioniService.execute(mentore, h.getId());

        assertEquals(1, elenco.size());
    }

    @Test
    void rifiutaSeRichiedenteNonAssegnatoAQuestoHackathon() {
        Hackathon h = creaHackathon();

        assertThrows(IllegalArgumentException.class, () ->
                visualizzaSottomissioniService.execute(estraneo, h.getId()));
    }

    @Test
    void rifiutaSeHackathonNonTrovato() {
        assertThrows(IllegalArgumentException.class, () ->
                visualizzaSottomissioniService.execute(organizzatore, UUID.randomUUID()));
    }

    @Test
    void elencoVuotoSeNessunTeamIscritto() {
        Hackathon h = creaHackathon();

        List<VisualizzaSottomissioniService.VoceSottomissione> elenco =
                visualizzaSottomissioniService.execute(organizzatore, h.getId());

        assertTrue(elenco.isEmpty());
    }
}