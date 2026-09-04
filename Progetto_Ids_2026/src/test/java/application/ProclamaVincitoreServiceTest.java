package application;

import domain.enums.RuoloStaff;
import domain.models.*;
import domain.repository.HackathonRepository;
import domain.repository.TeamRepository;
import infrastructure.repository.InMemoryHackathonRepository;
import infrastructure.repository.InMemoryInvitoRepository;
import infrastructure.repository.InMemoryTeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProclamaVincitoreServiceTest {

    private HackathonRepository hackathonRepository;
    private TeamRepository teamRepository;
    private CreateHackathonService createHackathonService;
    private CreaTeamService creaTeamService;
    private IscriviTeamService iscriviTeamService;
    private ProclamaVincitoreService proclamaVincitoreService;

    private MembroStaff organizzatore;
    private MembroStaff giudice;
    private MembroStaff mentore;

    @BeforeEach
    void setUp() {
        hackathonRepository = new InMemoryHackathonRepository();
        teamRepository = new InMemoryTeamRepository();
        createHackathonService = new CreateHackathonService(hackathonRepository);
        creaTeamService = new CreaTeamService(teamRepository, new InvitaMembriService(new InMemoryInvitoRepository()));
        iscriviTeamService = new IscriviTeamService(hackathonRepository, teamRepository);
        proclamaVincitoreService = new ProclamaVincitoreService(hackathonRepository);

        organizzatore = new MembroStaff(RuoloStaff.ORGANIZZATORE, "Mario", "mario@staff.test");
        giudice = new MembroStaff(RuoloStaff.GIUDICE, "Luigi", "luigi@staff.test");
        mentore = new MembroStaff(RuoloStaff.MENTORE, "Peach", "peach@staff.test");
    }

    private HackathonData datiValidi(int maxTeam) {
        return HackathonData.builder()
                .nome("HackHub Test")
                .regolamento("Regolamento di prova")
                .luogo("Pesaro")
                .dataInizio(LocalDate.now().plusDays(30))
                .dataFine(LocalDate.now().plusDays(32))
                .scadenzaIscrizioni(LocalDate.now().plusDays(20))
                .premio(500.0)
                .maxTeam(maxTeam)
                .build();
    }

    private Hackathon creaHackathon() {
        return createHackathonService.execute(organizzatore, datiValidi(10), giudice, List.of(mentore));
    }

    private Team creaEIscriviTeam(String nome, Hackathon hackathon) {
        Utente capo = new Utente(nome, nome.toLowerCase() + "@test.it");
        Team team = creaTeamService.execute(capo, nome, List.of()).team;
        iscriviTeamService.execute(capo, team.getId(), hackathon.getId());
        return team;
    }

    private void assegnaSottomissione(Team team, Hackathon hackathon, int punteggio) {
        Sottomissione s = new Sottomissione(team, hackathon, "https://github.com/" + team.getNome() + "/progetto");
        s.valuta(punteggio);
        team.setSottomissioneAttuale(s);
    }

    @Test
    void determinaVincitoreConPunteggioPiuAlto() {
        Hackathon h = creaHackathon();
        Team teamA = creaEIscriviTeam("TeamA", h);
        Team teamB = creaEIscriviTeam("TeamB", h);
        assegnaSottomissione(teamA, h, 80);
        assegnaSottomissione(teamB, h, 95);

        ProclamaVincitoreService.RisultatoProclamazione risultato =
                proclamaVincitoreService.execute(h.getId(), null);

        assertFalse(risultato.richiedeSceltaGiudice);
        assertEquals(teamB, risultato.vincitore);
        assertTrue(risultato.candidatiInParita.isEmpty());
    }

    @Test
    void segnalaPareggioSenzaSceltaGiudice() {
        Hackathon h = creaHackathon();
        Team teamA = creaEIscriviTeam("TeamA", h);
        Team teamB = creaEIscriviTeam("TeamB", h);
        assegnaSottomissione(teamA, h, 90);
        assegnaSottomissione(teamB, h, 90);

        ProclamaVincitoreService.RisultatoProclamazione risultato =
                proclamaVincitoreService.execute(h.getId(), null);

        assertTrue(risultato.richiedeSceltaGiudice);
        assertNull(risultato.vincitore);
        assertEquals(2, risultato.candidatiInParita.size());
        assertTrue(risultato.candidatiInParita.contains(teamA));
        assertTrue(risultato.candidatiInParita.contains(teamB));
    }

    @Test
    void risolvePareggioConSceltaGiudice() {
        Hackathon h = creaHackathon();
        Team teamA = creaEIscriviTeam("TeamA", h);
        Team teamB = creaEIscriviTeam("TeamB", h);
        assegnaSottomissione(teamA, h, 90);
        assegnaSottomissione(teamB, h, 90);

        ProclamaVincitoreService.RisultatoProclamazione risultato =
                proclamaVincitoreService.execute(h.getId(), teamA);

        assertFalse(risultato.richiedeSceltaGiudice);
        assertEquals(teamA, risultato.vincitore);
    }

    @Test
    void rifiutaSceltaGiudiceNonTraICandidati() {
        Hackathon h = creaHackathon();
        Team teamA = creaEIscriviTeam("TeamA", h);
        Team teamB = creaEIscriviTeam("TeamB", h);
        Team teamC = creaEIscriviTeam("TeamC", h);
        assegnaSottomissione(teamA, h, 90);
        assegnaSottomissione(teamB, h, 90);
        assegnaSottomissione(teamC, h, 50); // non in paritÃ 

        assertThrows(IllegalArgumentException.class, () ->
                proclamaVincitoreService.execute(h.getId(), teamC));
    }

    @Test
    void rifiutaSeUnTeamNonHaSottomissione() {
        Hackathon h = creaHackathon();
        Team teamA = creaEIscriviTeam("TeamA", h);
        creaEIscriviTeam("TeamB", h); // nessuna sottomissione assegnata
        assegnaSottomissione(teamA, h, 80);

        assertThrows(IllegalStateException.class, () ->
                proclamaVincitoreService.execute(h.getId(), null));
    }

    @Test
    void rifiutaSeUnaSottomissioneNonEValutata() {
        Hackathon h = creaHackathon();
        Team teamA = creaEIscriviTeam("TeamA", h);
        Team teamB = creaEIscriviTeam("TeamB", h);
        assegnaSottomissione(teamA, h, 80);

        Sottomissione nonValutata = new Sottomissione(teamB, h, "https://github.com/TeamB/progetto");
        teamB.setSottomissioneAttuale(nonValutata); // mai valutata

        assertThrows(IllegalStateException.class, () ->
                proclamaVincitoreService.execute(h.getId(), null));
    }

    @Test
    void rifiutaSeNessunTeamIscritto() {
        Hackathon h = creaHackathon();

        assertThrows(IllegalStateException.class, () ->
                proclamaVincitoreService.execute(h.getId(), null));
    }

    @Test
    void rifiutaSeHackathonNonTrovato() {
        assertThrows(IllegalArgumentException.class, () ->
                proclamaVincitoreService.execute(UUID.randomUUID(), null));
    }
}