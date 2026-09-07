package application;

import domain.models.Team;
import domain.models.Utente;
import domain.repository.TeamRepository;
import infrastructure.repository.InMemoryInvitoRepository;
import infrastructure.repository.InMemoryTeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class LasciareTeamServiceTest {

    private TeamRepository teamRepository;
    private LasciareTeamService lasciareTeamService;

    private Utente capo;
    private Utente membroA;
    private Utente membroB;

    @BeforeEach
    void setUp() {
        teamRepository = new InMemoryTeamRepository();
        // Random fisso: nextInt(...) restituisce sempre 0, sceglie sempre il primo
        // membro rimasto, per rendere il test deterministico.
        lasciareTeamService = new LasciareTeamService(teamRepository, new Random(0) {
            @Override
            public int nextInt(int bound) { return 0; }
        });

        capo = new Utente("Mario", "mario@test.it");
        membroA = new Utente("Luigi", "luigi@test.it");
        membroB = new Utente("Peach", "peach@test.it");
    }

    @Test
    void capoLasciaIlTeamConAltriMembriElezioneNuovoCapo() {
        // team con capo + 2 membri effettivi (aggiunti direttamente via dominio per semplicità del test)
        Team team = new Team("Team Y", capo);
        team.aggiungiMembro(membroA);
        team.aggiungiMembro(membroB);
        capo.setTeamAttuale(team);
        membroA.setTeamAttuale(team);
        membroB.setTeamAttuale(team);
        teamRepository.save(team);

        Utente nuovoCapo = lasciareTeamService.execute(capo);

        assertEquals(membroA, nuovoCapo); // Random forzato a 0 -> primo membro rimasto
        assertNull(capo.getTeamAttuale());
        assertEquals(membroA, team.getCapoTeam());
        assertFalse(team.getMembri().contains(membroA));
        assertTrue(team.getMembri().contains(membroB));
    }

    @Test
    void capoUnicoMembroScioglieIlTeam() {
        Team team = new Team("Team Solo", capo);
        capo.setTeamAttuale(team);
        teamRepository.save(team);

        Utente nuovoCapo = lasciareTeamService.execute(capo);

        assertNull(nuovoCapo);
        assertNull(capo.getTeamAttuale());
        assertTrue(teamRepository.findById(team.getId()).isEmpty());
    }

    @Test
    void membroNonCapoLasciaIlTeamSenzaCambiareCapo() {
        Team team = new Team("Team Z", capo);
        team.aggiungiMembro(membroA);
        capo.setTeamAttuale(team);
        membroA.setTeamAttuale(team);
        teamRepository.save(team);

        Utente nuovoCapo = lasciareTeamService.execute(membroA);

        assertNull(nuovoCapo);
        assertNull(membroA.getTeamAttuale());
        assertEquals(capo, team.getCapoTeam());
        assertFalse(team.getMembri().contains(membroA));
    }

    @Test
    void rifiutaSeUtenteNonInAlcunTeam() {
        assertThrows(IllegalStateException.class, () -> lasciareTeamService.execute(capo));
    }
}
