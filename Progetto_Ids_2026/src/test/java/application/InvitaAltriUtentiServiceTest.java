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

class InvitaAltriUtentiServiceTest {

    private TeamRepository teamRepository;
    private InvitoRepository invitoRepository;
    private InvitaMembriService invitaMembriService;
    private CreaTeamService creaTeamService;
    private InvitaAltriUtentiService invitaAltriUtentiService;
    private VisualizzaInvitiService visualizzaInvitiService;

    private Utente capoTeam;
    private Utente altroUtente;
    private Utente nuovoInvitato;

    @BeforeEach
    void setUp() {
        teamRepository = new InMemoryTeamRepository();
        invitoRepository = new InMemoryInvitoRepository();
        invitaMembriService = new InvitaMembriService(invitoRepository);
        creaTeamService = new CreaTeamService(teamRepository, invitaMembriService);
        invitaAltriUtentiService = new InvitaAltriUtentiService(teamRepository, invitaMembriService);
        visualizzaInvitiService = new VisualizzaInvitiService(invitoRepository);

        capoTeam = new Utente("Mario", "mario@test.it");
        altroUtente = new Utente("Wario", "wario@test.it");
        nuovoInvitato = new Utente("Toad", "toad@test.it");
    }

    private Team creaTeamVuoto() {
        return creaTeamService.execute(capoTeam, "I Fantastici", List.of()).team;
    }

    @Test
    void invitaNuovoUtenteConSuccesso() {
        Team team = creaTeamVuoto();

        InvitaMembriService.RisultatoInviti risultato =
                invitaAltriUtentiService.execute(capoTeam, team.getId(), List.of(nuovoInvitato));

        assertEquals(1, risultato.creati.size());
        assertTrue(risultato.esclusi.isEmpty());

        List<Invito> inviti = visualizzaInvitiService.execute(nuovoInvitato);
        assertEquals(1, inviti.size());
        assertEquals(team, inviti.get(0).getTeam());
    }

    @Test
    void rifiutaSeRichiedenteNonCapoTeam() {
        Team team = creaTeamVuoto();

        assertThrows(IllegalArgumentException.class, () ->
                invitaAltriUtentiService.execute(altroUtente, team.getId(), List.of(nuovoInvitato)));
    }

    @Test
    void rifiutaSeTeamNonTrovato() {
        assertThrows(IllegalArgumentException.class, () ->
                invitaAltriUtentiService.execute(capoTeam, UUID.randomUUID(), List.of(nuovoInvitato)));
    }

    @Test
    void escludeInvitatoGiaInUnAltroTeam() {
        Team team = creaTeamVuoto();
        // altroUtente diventa capo di un altro team, quindi risulta già impegnato
        creaTeamService.execute(altroUtente, "Team Preesistente", List.of());

        InvitaMembriService.RisultatoInviti risultato =
                invitaAltriUtentiService.execute(capoTeam, team.getId(), List.of(altroUtente, nuovoInvitato));

        assertEquals(1, risultato.creati.size());
        assertEquals(1, risultato.esclusi.size());
        assertEquals(altroUtente, risultato.esclusi.get(0));
    }

    @Test
    void nessunInvitoSeListaVuota() {
        Team team = creaTeamVuoto();

        InvitaMembriService.RisultatoInviti risultato =
                invitaAltriUtentiService.execute(capoTeam, team.getId(), List.of());

        assertTrue(risultato.creati.isEmpty());
        assertTrue(risultato.esclusi.isEmpty());
    }

    @Test
    void funzionaAncheDopoLaCreazioneIniziale() {
        // Il capo team crea il team con un invitato iniziale...
        Utente invitatoIniziale = new Utente("Peach", "peach@test.it");
        Team team = creaTeamService.execute(capoTeam, "Team Esteso", List.of(invitatoIniziale)).team;

        // ...e più avanti invita anche un altro utente, in un'azione separata
        InvitaMembriService.RisultatoInviti risultato =
                invitaAltriUtentiService.execute(capoTeam, team.getId(), List.of(nuovoInvitato));

        assertEquals(1, risultato.creati.size());

        // entrambi gli inviti (iniziale + successivo) devono coesistere
        assertEquals(1, visualizzaInvitiService.execute(invitatoIniziale).size());
        assertEquals(1, visualizzaInvitiService.execute(nuovoInvitato).size());
    }
}