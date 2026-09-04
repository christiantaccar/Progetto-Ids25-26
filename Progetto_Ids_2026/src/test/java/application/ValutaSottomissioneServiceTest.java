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

class ValutaSottomissioneServiceTest {

    private HackathonRepository hackathonRepository;
    private TeamRepository teamRepository;
    private CreateHackathonService createHackathonService;
    private ValutaSottomissioneService valutaSottomissioneService;

    private MembroStaff organizzatore;
    private MembroStaff giudice;
    private MembroStaff altroGiudice;
    private MembroStaff mentore;

    @BeforeEach
    void setUp() {
        hackathonRepository = new InMemoryHackathonRepository();
        teamRepository = new InMemoryTeamRepository();
        createHackathonService = new CreateHackathonService(hackathonRepository);
        valutaSottomissioneService = new ValutaSottomissioneService(teamRepository);

        organizzatore = new MembroStaff(RuoloStaff.ORGANIZZATORE, "Mario", "mario@staff.test");
        giudice = new MembroStaff(RuoloStaff.GIUDICE, "Luigi", "luigi@staff.test");
        altroGiudice = new MembroStaff(RuoloStaff.GIUDICE, "Wario", "wario@staff.test");
        mentore = new MembroStaff(RuoloStaff.MENTORE, "Peach", "peach@staff.test");
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

    /** Crea l'hackathon, iscrive un team con una sottomissione non ancora valutata, poi forza lo stato IN_VALUTAZIONE. */
    private Team creaHackathonInValutazioneConSottomissione(boolean conSottomissione) {
        Hackathon h = createHackathonService.execute(organizzatore, datiValidi(), giudice, List.of(mentore));

        Utente capo = new Utente("Anna", "anna@test.it");
        Team team = new Team("TeamA", capo);
        capo.setTeamAttuale(team);
        h.iscriviTeam(team);              // permesso: ancora IN_ISCRIZIONE
        team.setHackathonAttuale(h);

        if (conSottomissione) {
            Sottomissione s = new Sottomissione(team, h, "https://github.com/TeamA/progetto");
            team.setSottomissioneAttuale(s);
        }

        teamRepository.save(team);

        h.aggiornaStato(LocalDate.of(2026, 9, 5)); // dopo dataFine -> IN_VALUTAZIONE

        return team;
    }

    @Test
    void valutaConSuccesso() {
        Team team = creaHackathonInValutazioneConSottomissione(true);

        Sottomissione sottomissione = valutaSottomissioneService.execute(giudice, team.getId(), 85);

        assertTrue(sottomissione.isValutata());
        assertEquals(85, sottomissione.getPunteggio());
        assertSame(sottomissione, team.getSottomissioneAttuale());
    }

    @Test
    void rifiutaSeTeamNonTrovato() {
        assertThrows(IllegalArgumentException.class, () ->
                valutaSottomissioneService.execute(giudice, UUID.randomUUID(), 80));
    }

    @Test
    void rifiutaSeTeamNonIscrittoAdAlcunHackathon() {
        Utente capo = new Utente("Anna", "anna@test.it");
        Team team = new Team("TeamA", capo);
        capo.setTeamAttuale(team);
        teamRepository.save(team); // nessun hackathonAttuale

        assertThrows(IllegalStateException.class, () ->
                valutaSottomissioneService.execute(giudice, team.getId(), 80));
    }

    @Test
    void rifiutaSeRichiedenteNonEIlGiudiceAssegnato() {
        Team team = creaHackathonInValutazioneConSottomissione(true);

        assertThrows(IllegalArgumentException.class, () ->
                valutaSottomissioneService.execute(altroGiudice, team.getId(), 80));
    }

    @Test
    void rifiutaSeHackathonNonInStatoInValutazione() {
        Hackathon h = createHackathonService.execute(organizzatore, datiValidi(), giudice, List.of(mentore));

        Utente capo = new Utente("Anna", "anna@test.it");
        Team team = new Team("TeamA", capo);
        capo.setTeamAttuale(team);
        h.iscriviTeam(team);
        team.setHackathonAttuale(h);
        Sottomissione s = new Sottomissione(team, h, "https://github.com/TeamA/progetto");
        team.setSottomissioneAttuale(s);
        teamRepository.save(team);
        // nessuna transizione forzata: hackathon resta IN_ISCRIZIONE

        assertThrows(IllegalStateException.class, () ->
                valutaSottomissioneService.execute(giudice, team.getId(), 80));
    }

    @Test
    void rifiutaSeIlTeamNonHaInviatoNessunaSottomissione() {
        Team team = creaHackathonInValutazioneConSottomissione(false);

        assertThrows(IllegalStateException.class, () ->
                valutaSottomissioneService.execute(giudice, team.getId(), 80));
    }

    @Test
    void rifiutaRivalutazione() {
        Team team = creaHackathonInValutazioneConSottomissione(true);
        valutaSottomissioneService.execute(giudice, team.getId(), 80);

        assertThrows(IllegalStateException.class, () ->
                valutaSottomissioneService.execute(giudice, team.getId(), 90));
    }

    @Test
    void rifiutaPunteggioFuoriRange() {
        Team team = creaHackathonInValutazioneConSottomissione(true);

        assertThrows(IllegalArgumentException.class, () ->
                valutaSottomissioneService.execute(giudice, team.getId(), 150));
    }
}