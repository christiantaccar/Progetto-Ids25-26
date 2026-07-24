package application;

import domain.models.Invito;
import domain.models.Team;
import domain.models.Utente;
import domain.repository.InvitoRepository;
import domain.repository.TeamRepository;
import infrastructure.repository.InMemoryInvitoRepository;
import infrastructure.repository.InMemoryTeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TeamServiceTest {

    private TeamRepository teamRepository;
    private InvitoRepository invitoRepository;
    private InvitaMembriService invitaMembriService;
    private CreaTeamService creaTeamService;
    private UnisciTeamService unisciTeamService;
    private VisualizzaInvitiService visualizzaInvitiService;

    private Utente creatore;
    private Utente invitatoA;
    private Utente invitatoB;

    @BeforeEach
    void setUp() {
        teamRepository = new InMemoryTeamRepository();
        invitoRepository = new InMemoryInvitoRepository();
        invitaMembriService = new InvitaMembriService(invitoRepository);
        creaTeamService = new CreaTeamService(teamRepository, invitaMembriService);
        unisciTeamService = new UnisciTeamService(invitoRepository, teamRepository);
        visualizzaInvitiService = new VisualizzaInvitiService(invitoRepository);

        creatore = new Utente("Mario", "mario@test.it");
        invitatoA = new Utente("Luigi", "luigi@test.it");
        invitatoB = new Utente("Peach", "peach@test.it");
    }

    @Test
    void creaTeamConSuccessoEGeneraInviti() {
        CreaTeamService.RisultatoCreazione risultato =
                creaTeamService.execute(creatore, "I Fantastici", List.of(invitatoA, invitatoB));

        Team team = risultato.team;
        assertEquals(creatore, team.getCapoTeam());
        assertTrue(team.getMembri().isEmpty()); // nessuno ha ancora accettato
        assertTrue(risultato.esclusi.isEmpty());
        assertTrue(creatore.isInTeam());

        List<Invito> invitiA = visualizzaInvitiService.execute(invitatoA);
        assertEquals(1, invitiA.size());
        assertEquals(team, invitiA.get(0).getTeam());
    }

    @Test
    void rifiutaCreazioneSeCreatoreGiaInUnTeam() {
        creaTeamService.execute(creatore, "Team 1", List.of());

        assertThrows(IllegalStateException.class, () ->
                creaTeamService.execute(creatore, "Team 2", List.of()));
    }

    @Test
    void escludeInvitatoGiaInUnAltroTeam() {
        creaTeamService.execute(invitatoA, "Team Preesistente", List.of());

        CreaTeamService.RisultatoCreazione risultato =
                creaTeamService.execute(creatore, "Nuovo Team", List.of(invitatoA, invitatoB));

        assertEquals(1, risultato.esclusi.size());
        assertEquals(invitatoA, risultato.esclusi.get(0));

        List<Invito> invitiB = visualizzaInvitiService.execute(invitatoB);
        assertEquals(1, invitiB.size());
    }

    @Test
    void creaTeamSenzaInvitati() {
        CreaTeamService.RisultatoCreazione risultato =
                creaTeamService.execute(creatore, "Solo io", null);

        assertNotNull(risultato.team);
        assertTrue(risultato.esclusi.isEmpty());
    }

    @Test
    void accettaInvitoConSuccesso() {
        CreaTeamService.RisultatoCreazione risultato =
                creaTeamService.execute(creatore, "Team X", List.of(invitatoA));
        Invito invito = visualizzaInvitiService.execute(invitatoA).get(0);

        unisciTeamService.execute(invitatoA, invito.getId());

        assertTrue(invitatoA.isInTeam());
        assertEquals(risultato.team, invitatoA.getTeamAttuale());
        assertTrue(risultato.team.getMembri().contains(invitatoA));
        assertTrue(visualizzaInvitiService.execute(invitatoA).isEmpty());
    }

    @Test
    void rifiutaAccettazioneSeInvitoNonEsiste() {
        assertThrows(IllegalArgumentException.class, () ->
                unisciTeamService.execute(invitatoA, UUID.randomUUID()));
    }

    @Test
    void rifiutaAccettazioneSeUtenteGiaInUnTeam() {
        creaTeamService.execute(creatore, "Team X", List.of(invitatoA));
        Invito invito = visualizzaInvitiService.execute(invitatoA).get(0);

        creaTeamService.execute(invitatoA, "Altro team", List.of()); // invitatoA entra in un altro team nel frattempo

        assertThrows(IllegalStateException.class, () ->
                unisciTeamService.execute(invitatoA, invito.getId()));
    }

    @Test
    void accettareUnInvitoInvalidaGliAltriInvitiPendenti() {
        creaTeamService.execute(creatore, "Team A", List.of(invitatoA));
        Utente altroCreatore = new Utente("Wario", "wario@test.it");
        creaTeamService.execute(altroCreatore, "Team B", List.of(invitatoA));

        List<Invito> invitiPrima = visualizzaInvitiService.execute(invitatoA);
        assertEquals(2, invitiPrima.size());

        unisciTeamService.execute(invitatoA, invitiPrima.get(0).getId());

        List<Invito> invitiDopo = visualizzaInvitiService.execute(invitatoA);
        assertTrue(invitiDopo.isEmpty()); // l'altro invito è stato rifiutato automaticamente
    }

    @Test
    void visualizzaInvitiVuotoSeNessunInvito() {
        List<Invito> inviti = visualizzaInvitiService.execute(creatore);
        assertTrue(inviti.isEmpty());
    }
}