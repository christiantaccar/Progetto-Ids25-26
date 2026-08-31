package application;

import domain.enums.RuoloStaff;
import domain.models.*;
import domain.repository.HackathonRepository;
import domain.repository.SottomissioneRepository;
import domain.repository.TeamRepository;
import infrastructure.repository.InMemoryHackathonRepository;
import infrastructure.repository.InMemorySottomissioneRepository;
import infrastructure.repository.InMemoryTeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InviaSottomissioneServiceTest {

    private HackathonRepository hackathonRepository;
    private TeamRepository teamRepository;
    private SottomissioneRepository sottomissioneRepository;
    private CreateHackathonService createHackathonService;
    private InviaSottomissioneService inviaSottomissioneService;

    private MembroStaff organizzatore;
    private MembroStaff giudice;
    private MembroStaff mentore;

    @BeforeEach
    void setUp() {
        hackathonRepository = new InMemoryHackathonRepository();
        teamRepository = new InMemoryTeamRepository();
        sottomissioneRepository = new InMemorySottomissioneRepository();
        createHackathonService = new CreateHackathonService(hackathonRepository);
        inviaSottomissioneService = new InviaSottomissioneService(teamRepository, sottomissioneRepository);

        organizzatore = new MembroStaff(RuoloStaff.ORGANIZZATORE, "Mario");
        giudice = new MembroStaff(RuoloStaff.GIUDICE, "Luigi");
        mentore = new MembroStaff(RuoloStaff.MENTORE, "Peach");
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

    /** Crea l'hackathon, iscrive un team (mentre è ancora IN_ISCRIZIONE) e poi forza la transizione a IN_CORSO. */
    private Team creaHackathonInCorsoConTeamIscritto(String nomeTeam) {
        Hackathon h = createHackathonService.execute(organizzatore, datiValidi(), giudice, List.of(mentore));

        Utente capo = new Utente(nomeTeam, nomeTeam.toLowerCase() + "@test.it");
        Team team = new Team(nomeTeam, capo);
        capo.setTeamAttuale(team);
        h.iscriviTeam(team);              // ancora permesso: hackathon appena creato è IN_ISCRIZIONE
        team.setHackathonAttuale(h);
        teamRepository.save(team);

        h.aggiornaStato(LocalDate.of(2026, 9, 2)); // forza la transizione a IN_CORSO (data tra inizio e fine)

        return team;
    }

    @Test
    void inviaSottomissioneConSuccesso() {
        Team team = creaHackathonInCorsoConTeamIscritto("TeamA");

        Sottomissione sottomissione = inviaSottomissioneService.execute(
                team.getCapoTeam(), team.getId(), "https://github.com/TeamA/progetto");

        assertNotNull(sottomissione);
        assertEquals(team, sottomissione.getTeam());
        assertEquals("https://github.com/TeamA/progetto", sottomissione.getLink());
        assertFalse(sottomissione.isValutata());
        assertEquals(sottomissione, team.getSottomissioneAttuale());
    }

    @Test
    void reinvioSovrascriveLaSottomissionePrecedente() {
        Team team = creaHackathonInCorsoConTeamIscritto("TeamA");

        Sottomissione prima = inviaSottomissioneService.execute(
                team.getCapoTeam(), team.getId(), "https://github.com/TeamA/v1");
        Sottomissione seconda = inviaSottomissioneService.execute(
                team.getCapoTeam(), team.getId(), "https://github.com/TeamA/v2");

        assertNotEquals(prima, seconda);
        assertEquals(seconda, team.getSottomissioneAttuale());
        assertEquals("https://github.com/TeamA/v2", team.getSottomissioneAttuale().getLink());
    }

    @Test
    void rifiutaSeRichiedenteNonCapoTeam() {
        Team team = creaHackathonInCorsoConTeamIscritto("TeamA");
        Utente altroUtente = new Utente("Wario", "wario@test.it");

        assertThrows(IllegalArgumentException.class, () ->
                inviaSottomissioneService.execute(altroUtente, team.getId(), "https://github.com/TeamA/progetto"));
    }

    @Test
    void rifiutaSeTeamNonTrovato() {
        Utente utente = new Utente("Mario", "mario@test.it");

        assertThrows(IllegalArgumentException.class, () ->
                inviaSottomissioneService.execute(utente, UUID.randomUUID(), "https://github.com/x/y"));
    }

    @Test
    void rifiutaSeTeamNonIscrittoAdAlcunHackathon() {
        Utente capo = new Utente("Mario", "mario@test.it");
        Team team = new Team("TeamA", capo);
        capo.setTeamAttuale(team);
        teamRepository.save(team); // nessun hackathonAttuale assegnato

        assertThrows(IllegalStateException.class, () ->
                inviaSottomissioneService.execute(capo, team.getId(), "https://github.com/TeamA/progetto"));
    }

    @Test
    void rifiutaSeHackathonNonInStatoInCorso() {
        // Hackathon appena creato: resta IN_ISCRIZIONE, nessuna transizione forzata.
        Hackathon h = createHackathonService.execute(organizzatore, datiValidi(), giudice, List.of(mentore));

        Utente capo = new Utente("Mario", "mario@test.it");
        Team team = new Team("TeamA", capo);
        capo.setTeamAttuale(team);
        h.iscriviTeam(team);
        team.setHackathonAttuale(h);
        teamRepository.save(team);

        assertThrows(IllegalStateException.class, () ->
                inviaSottomissioneService.execute(capo, team.getId(), "https://github.com/TeamA/progetto"));
    }

    @Test
    void rifiutaLinkVuoto() {
        Team team = creaHackathonInCorsoConTeamIscritto("TeamA");

        assertThrows(IllegalArgumentException.class, () ->
                inviaSottomissioneService.execute(team.getCapoTeam(), team.getId(), "   "));
    }
}