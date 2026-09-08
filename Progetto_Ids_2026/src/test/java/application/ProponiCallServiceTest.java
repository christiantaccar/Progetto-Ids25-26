package application;

import application.observer.NotificaControparteObserver;
import domain.enums.RuoloStaff;
import domain.enums.StatoCallProposta;
import domain.models.*;
import domain.repository.CallPropostaRepository;
import domain.repository.HackathonRepository;
import domain.repository.TeamRepository;
import infrastructure.notifica.NotificheInMemory;
import infrastructure.repository.InMemoryCallPropostaRepository;
import infrastructure.repository.InMemoryHackathonRepository;
import infrastructure.repository.InMemoryTeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProponiCallServiceTest {

    private TeamRepository teamRepository;
    private CallPropostaRepository callPropostaRepository;
    private HackathonRepository hackathonRepository;
    private NotificheInMemory notifiche;
    private ProponiCallService service;

    private MembroStaff organizzatore;
    private MembroStaff giudice;
    private MembroStaff mentore;
    private MembroStaff altroMentore;

    private Team team;
    private LocalDateTime dataOra;

    @BeforeEach
    void setUp() {
        teamRepository = new InMemoryTeamRepository();
        callPropostaRepository = new InMemoryCallPropostaRepository();
        hackathonRepository = new InMemoryHackathonRepository();
        notifiche = new NotificheInMemory();

        GestoreNotificheCall gestore = new GestoreNotificheCall();
        gestore.registra(new NotificaControparteObserver(notifiche));

        service = new ProponiCallService(teamRepository, callPropostaRepository, gestore);

        organizzatore = new MembroStaff(RuoloStaff.ORGANIZZATORE, "Mario", "mario@staff.test");
        giudice = new MembroStaff(RuoloStaff.GIUDICE, "Luigi", "luigi@staff.test");
        mentore = new MembroStaff(RuoloStaff.MENTORE, "Peach", "peach@staff.test");
        altroMentore = new MembroStaff(RuoloStaff.MENTORE, "Daisy", "daisy@staff.test");

        Utente capo = new Utente("Anna", "anna@test.it");
        team = new Team("Alpha", capo);
        teamRepository.save(team);

        Hackathon hackathon = new CreateHackathonService(hackathonRepository)
                .execute(organizzatore, datiValidi(), giudice, List.of(mentore));
        team.setHackathonAttuale(hackathon);

        dataOra = LocalDateTime.of(2026, 9, 2, 15, 0);
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

    // ====== SCENARIO PRINCIPALE ======

    @Test
    void proponeUnaCallAlTeam() {
        CallProposta proposta = service.execute(mentore, team.getId(), dataOra, "https://meet.test/abc");

        assertNotNull(proposta);
        assertEquals(StatoCallProposta.PENDENTE, proposta.getStato());
        assertEquals(team, proposta.getTeam());
        assertEquals(mentore, proposta.getMentore());
    }

    @Test
    void laPropostaVienePersistita() {
        CallProposta proposta = service.execute(mentore, team.getId(), dataOra, "https://meet.test/abc");

        assertTrue(callPropostaRepository.findById(proposta.getId()).isPresent());
    }

    @Test
    void ilCapoTeamVieneNotificato() {
        service.execute(mentore, team.getId(), dataOra, "https://meet.test/abc");

        assertEquals(1, notifiche.getInviate().size());
        assertEquals("anna@test.it", notifiche.getInviate().get(0).destinatario.getEmail());
    }

    // ====== ESTENSIONI ======

    @Test
    void rifiutaUnTeamInesistente() {
        assertThrows(IllegalArgumentException.class,
                () -> service.execute(mentore, UUID.randomUUID(), dataOra, "https://meet.test/abc"));
    }

    @Test
    void rifiutaUnTeamNonIscrittoAdAlcunHackathon() {
        Team senzaHackathon = new Team("Beta", new Utente("Bea", "bea@test.it"));
        teamRepository.save(senzaHackathon);

        assertThrows(IllegalStateException.class,
                () -> service.execute(mentore, senzaHackathon.getId(), dataOra, "https://meet.test/abc"));
    }

    @Test
    void rifiutaUnMentoreNonAssegnatoAQuestoHackathon() {
        assertThrows(IllegalArgumentException.class,
                () -> service.execute(altroMentore, team.getId(), dataOra, "https://meet.test/abc"));
    }

    @Test
    void rifiutaUnaSecondaPropostaPendenteDalloStessoMentore() {
        service.execute(mentore, team.getId(), dataOra, "https://meet.test/abc");

        assertThrows(IllegalStateException.class,
                () -> service.execute(mentore, team.getId(), dataOra.plusDays(1), "https://meet.test/def"));
    }

    @Test
    void unaPropostaFallitaNonNotificaNessuno() {
        assertThrows(IllegalArgumentException.class,
                () -> service.execute(altroMentore, team.getId(), dataOra, "https://meet.test/abc"));

        assertTrue(notifiche.getInviate().isEmpty());
    }

    @Test
    void soloUnMentorePuoEssereAutoreDiUnaProposta() {
        assertThrows(IllegalArgumentException.class,
                () -> new CallProposta(giudice, team, dataOra, "https://meet.test/abc"));
    }
}
