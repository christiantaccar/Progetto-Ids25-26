package application;

import application.observer.CalendarioObserver;
import domain.observer.CallPropostaObserver;
import application.observer.NotificaControparteObserver;
import domain.enums.RuoloStaff;
import domain.enums.StatoCallProposta;
import domain.models.*;
import domain.repository.CallPropostaRepository;
import domain.repository.HackathonRepository;
import domain.repository.TeamRepository;
import infrastructure.calendar.CalendarioSimulato;
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

class RispondiCallPropostaServiceTest {

    private TeamRepository teamRepository;
    private CallPropostaRepository callPropostaRepository;
    private HackathonRepository hackathonRepository;
    private NotificheInMemory notifiche;
    private CalendarioSimulato calendario;
    private GestoreNotificheCall gestore;

    private ProponiCallService proponiCallService;
    private RispondiCallPropostaService service;

    private MembroStaff mentore;
    private Utente capoTeam;
    private Utente altroUtente;
    private Team team;
    private CallProposta proposta;

    @BeforeEach
    void setUp() {
        teamRepository = new InMemoryTeamRepository();
        callPropostaRepository = new InMemoryCallPropostaRepository();
        hackathonRepository = new InMemoryHackathonRepository();
        notifiche = new NotificheInMemory();
        calendario = new CalendarioSimulato();

        gestore = new GestoreNotificheCall();
        gestore.registra(new NotificaControparteObserver(notifiche));
        gestore.registra(new CalendarioObserver(calendario));

        proponiCallService = new ProponiCallService(teamRepository, callPropostaRepository, gestore);
        service = new RispondiCallPropostaService(callPropostaRepository, gestore);

        MembroStaff organizzatore = new MembroStaff(RuoloStaff.ORGANIZZATORE, "Mario", "mario@staff.test");
        MembroStaff giudice = new MembroStaff(RuoloStaff.GIUDICE, "Luigi", "luigi@staff.test");
        mentore = new MembroStaff(RuoloStaff.MENTORE, "Peach", "peach@staff.test");

        capoTeam = new Utente("Anna", "anna@test.it");
        altroUtente = new Utente("Bea", "bea@test.it");

        team = new Team("Alpha", capoTeam);
        teamRepository.save(team);

        Hackathon hackathon = new CreateHackathonService(hackathonRepository)
                .execute(organizzatore, datiValidi(), giudice, List.of(mentore));
        team.setHackathonAttuale(hackathon);

        proposta = proponiCallService.execute(
                mentore, team.getId(), LocalDateTime.of(2026, 9, 2, 15, 0), "https://meet.test/abc");
        notifiche.svuota();
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

    // ====== ACCETTAZIONE ======

    @Test
    void ilCapoTeamAccettaLaProposta() {
        CallProposta risposta = service.execute(capoTeam, proposta.getId(), true);

        assertEquals(StatoCallProposta.ACCETTATA, risposta.getStato());
        assertFalse(risposta.isPendente());
    }

    @Test
    void accettandoLEventoFinisceSulCalendario() {
        service.execute(capoTeam, proposta.getId(), true);

        assertEquals(1, calendario.numeroEventi());
    }

    @Test
    void accettandoIlMentoreVieneNotificato() {
        service.execute(capoTeam, proposta.getId(), true);

        assertEquals(1, notifiche.getInviate().size());
        assertEquals("Peach", notifiche.getInviate().get(0).destinatario);
    }

    // ====== RIFIUTO ======

    @Test
    void ilCapoTeamRifiutaLaProposta() {
        CallProposta risposta = service.execute(capoTeam, proposta.getId(), false);

        assertEquals(StatoCallProposta.RIFIUTATA, risposta.getStato());
    }

    @Test
    void rifiutandoNessunEventoFinisceSulCalendario() {
        service.execute(capoTeam, proposta.getId(), false);

        assertEquals(0, calendario.numeroEventi());
    }

    @Test
    void rifiutandoIlMentoreVieneNotificato() {
        service.execute(capoTeam, proposta.getId(), false);

        assertEquals(1, notifiche.getInviate().size());
    }

    // ====== ESTENSIONI ======

    @Test
    void rifiutaUnaPropostaInesistente() {
        assertThrows(IllegalArgumentException.class,
                () -> service.execute(capoTeam, UUID.randomUUID(), true));
    }

    @Test
    void soloIlCapoTeamPuoRispondere() {
        assertThrows(IllegalArgumentException.class,
                () -> service.execute(altroUtente, proposta.getId(), true));
    }

    @Test
    void nonSiPuoRispondereDueVolte() {
        service.execute(capoTeam, proposta.getId(), true);

        assertThrows(IllegalStateException.class,
                () -> service.execute(capoTeam, proposta.getId(), false));
    }

    @Test
    void dopoLaRispostaIlMentorePuoProporneUnAltra() {
        service.execute(capoTeam, proposta.getId(), false);

        assertDoesNotThrow(() -> proponiCallService.execute(
                mentore, team.getId(), LocalDateTime.of(2026, 9, 3, 10, 0), "https://meet.test/def"));
    }

    // ====== UN OSSERVATORE CHE FALLISCE NON DEVE FERMARE NULLA ======

    /** Osservatore che solleva sempre un'eccezione, per verificare il contenimento nel Subject. */
    private static class OsservatoreRotto implements CallPropostaObserver {
        @Override
        public void onPropostaAccettata(CallProposta proposta) {
            throw new IllegalStateException("guasto simulato dell'osservatore");
        }
    }

    @Test
    void unOsservatoreRottoNonInvalidaLaRisposta() {
        GestoreNotificheCall gestoreConOsservatoreRotto = new GestoreNotificheCall();
        gestoreConOsservatoreRotto.registra(new OsservatoreRotto());

        CallProposta risposta = new RispondiCallPropostaService(callPropostaRepository, gestoreConOsservatoreRotto)
                .execute(capoTeam, proposta.getId(), true);

        assertEquals(StatoCallProposta.ACCETTATA, risposta.getStato());
    }

    @Test
    void unOsservatoreRottoNonImpedisceAgliAltriDiRicevereLEvento() {
        GestoreNotificheCall gestoreConOsservatoreRotto = new GestoreNotificheCall();
        gestoreConOsservatoreRotto.registra(new OsservatoreRotto());
        gestoreConOsservatoreRotto.registra(new NotificaControparteObserver(notifiche));

        new RispondiCallPropostaService(callPropostaRepository, gestoreConOsservatoreRotto)
                .execute(capoTeam, proposta.getId(), true);

        assertEquals(1, notifiche.getInviate().size());
    }
}
