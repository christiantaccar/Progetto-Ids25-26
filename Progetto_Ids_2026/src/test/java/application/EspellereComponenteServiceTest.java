package application;

import domain.models.Team;
import domain.models.Utente;
import domain.repository.TeamRepository;
import infrastructure.repository.InMemoryTeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EspellereComponenteServiceTest {

    private TeamRepository teamRepository;
    private EspellereComponenteService espellereComponenteService;

    private Utente capo;
    private Utente membro;

    @BeforeEach
    void setUp() {
        teamRepository = new InMemoryTeamRepository();
        espellereComponenteService = new EspellereComponenteService(teamRepository);

        capo = new Utente("Mario", "mario@test.it");
        membro = new Utente("Luigi", "luigi@test.it");
    }

    @Test
    void capoEspelleUnMembroConSuccesso() {
        Team team = new Team("Team X", capo);
        team.aggiungiMembro(membro);
        membro.setTeamAttuale(team);
        teamRepository.save(team);

        espellereComponenteService.execute(capo, team, membro);

        assertNull(membro.getTeamAttuale());
        assertFalse(team.getMembri().contains(membro));
    }

    @Test
    void rifiutaSeRichiedenteNonECapoTeam() {
        Team team = new Team("Team X", capo);
        Utente altroMembro = new Utente("Peach", "peach@test.it");
        team.aggiungiMembro(membro);
        team.aggiungiMembro(altroMembro);
        teamRepository.save(team);

        assertThrows(IllegalArgumentException.class, () ->
                espellereComponenteService.execute(membro, team, altroMembro));
    }

    @Test
    void rifiutaSeCapoTentaDiEspellereSeStesso() {
        Team team = new Team("Team X", capo);
        teamRepository.save(team);

        assertThrows(IllegalArgumentException.class, () ->
                espellereComponenteService.execute(capo, team, capo));
    }

    @Test
    void rifiutaSeUtenteNonEMembroDelTeam() {
        Team team = new Team("Team X", capo);
        teamRepository.save(team);
        Utente estraneo = new Utente("Wario", "wario@test.it");

        assertThrows(IllegalArgumentException.class, () ->
                espellereComponenteService.execute(capo, team, estraneo));
    }
}
