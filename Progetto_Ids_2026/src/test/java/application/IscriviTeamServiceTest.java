package application;

import domain.enums.RuoloStaff;
import domain.models.*;
import domain.repository.HackathonRepository;
import domain.repository.TeamRepository;
import infrastructure.repository.InMemoryHackathonRepository;
import infrastructure.repository.InMemoryTeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class IscriviTeamServiceTest {

    private HackathonRepository hackathonRepository;
    private TeamRepository teamRepository;
    private CreateHackathonService createHackathonService;
    private CreaTeamService creaTeamService;
    private IscriviTeamService iscriviTeamService;

    private MembroStaff organizzatore;
    private MembroStaff giudice;
    private MembroStaff mentore;
    private Utente capoTeam;

    @BeforeEach
    void setUp() {
        hackathonRepository = new InMemoryHackathonRepository();
        teamRepository = new InMemoryTeamRepository();
        createHackathonService = new CreateHackathonService(hackathonRepository);
        creaTeamService = new CreaTeamService(teamRepository, new InvitaMembriService(new infrastructure.repository.InMemoryInvitoRepository()));
        iscriviTeamService = new IscriviTeamService(hackathonRepository, teamRepository);

        organizzatore = new MembroStaff(RuoloStaff.ORGANIZZATORE, "Mario");
        giudice = new MembroStaff(RuoloStaff.GIUDICE, "Luigi");
        mentore = new MembroStaff(RuoloStaff.MENTORE, "Peach");
        capoTeam = new Utente("Toad", "toad@test.it");
    }

    private HackathonData datiHackathonConMaxTeam(int maxTeam) {
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

    private Hackathon creaHackathon(int maxTeam) {
        return createHackathonService.execute(organizzatore, datiHackathonConMaxTeam(maxTeam), giudice, List.of(mentore));
    }

    private Team creaTeam(Utente capo) {
        return creaTeamService.execute(capo, "I Fantastici", List.of()).team;
    }

    @Test
    void iscriveTeamConSuccesso() {
        Hackathon h = creaHackathon(10);
        Team team = creaTeam(capoTeam);

        iscriviTeamService.execute(capoTeam, team.getId(), h.getId());

        Hackathon hAggiornato = hackathonRepository.findById(h.getId()).orElseThrow();
        assertEquals(1, hAggiornato.getNumeroTeamIscritti());
        assertTrue(hAggiornato.getTeamIscritti().contains(team));
        assertEquals(h, team.getHackathonAttuale());
    }

    @Test
    void rifiutaSeChiamanteNonCapoTeam() {
        Hackathon h = creaHackathon(10);
        Team team = creaTeam(capoTeam);
        Utente altroUtente = new Utente("Wario", "wario@test.it");

        assertThrows(IllegalArgumentException.class, () ->
                iscriviTeamService.execute(altroUtente, team.getId(), h.getId()));
    }

    @Test
    void rifiutaSeTeamNonTrovato() {
        Hackathon h = creaHackathon(10);

        assertThrows(IllegalArgumentException.class, () ->
                iscriviTeamService.execute(capoTeam, UUID.randomUUID(), h.getId()));
    }

    @Test
    void rifiutaSeHackathonNonTrovato() {
        Team team = creaTeam(capoTeam);

        assertThrows(IllegalArgumentException.class, () ->
                iscriviTeamService.execute(capoTeam, team.getId(), UUID.randomUUID()));
    }

    @Test
    void rifiutaSeTeamGiaIscrittoAdAltroHackathonNonConcluso() {
        Hackathon h1 = creaHackathon(10);
        Hackathon h2 = creaHackathon(10);
        Team team = creaTeam(capoTeam);

        iscriviTeamService.execute(capoTeam, team.getId(), h1.getId());

        assertThrows(IllegalStateException.class, () ->
                iscriviTeamService.execute(capoTeam, team.getId(), h2.getId()));
    }

    @Test
    void rifiutaTeamTroppoGrande(){
        Hackathon h1 = creaHackathon(2);
        Team team = creaTeam(capoTeam);
        Utente u1= new Utente("Mario","mari@gmail.com");
        Utente u2= new Utente("Dario","dario@gmail.com");
        team.aggiungiMembro(u1);
        team.aggiungiMembro(u2);

        assertThrows(IllegalStateException.class, () ->
                iscriviTeamService.execute(capoTeam, team.getId(), h1.getId()));
    }
}