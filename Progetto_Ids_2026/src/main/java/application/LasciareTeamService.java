package application;

import domain.enums.StatoHackathon;
import domain.models.Hackathon;
import domain.models.Team;
import domain.models.Utente;
import domain.repository.TeamRepository;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * UC "Lasciare team".
 *
 * Gestisce tre casi distinti:
 *  - l'utente è un membro qualsiasi: viene semplicemente rimosso dal team;
 *  - l'utente è il capo team e restano altri membri: uno di essi viene
 *    scelto casualmente come nuovo capo;
 *  - l'utente è il capo team ed è l'unico membro rimasto: il team viene
 *    sciolto (non ha più senso un team senza componenti).
 */
public class LasciareTeamService {

    private final TeamRepository teamRepository;
    private final Random random;

    public LasciareTeamService(TeamRepository teamRepository) {
        this(teamRepository, new Random());
    }

    /** Costruttore secondario: consente di iniettare un Random deterministico nei test. */
    public LasciareTeamService(TeamRepository teamRepository, Random random) {
        this.teamRepository = Objects.requireNonNull(teamRepository, "TeamRepository obbligatorio");
        this.random = Objects.requireNonNull(random, "Random obbligatorio");
    }

    /**
     * Esegue l'uscita dell'utente dal team.
     *
     * @return il nuovo capo team se ne è stato eletto uno per sostituire
     *         l'utente uscente, altrimenti {@code null} (l'utente non era
     *         il capo, oppure il team è stato sciolto)
     */
    public Utente execute(Utente utente) {
        Objects.requireNonNull(utente, "Utente obbligatorio");

        Team team = utente.getTeamAttuale();
        if (team == null) {
            throw new IllegalStateException("Non fai parte di alcun team");
        }

        richiediHackathonAncoraInIscrizione(team);

        boolean eCapoTeam = utente.equals(team.getCapoTeam());

        if (!eCapoTeam) {
            // Caso A: membro qualsiasi lascia il team.
            team.rimuoviMembro(utente);
            utente.setTeamAttuale(null);
            teamRepository.save(team);
            return null;
        }

        List<Utente> membriRimasti = team.getMembri();

        if (membriRimasti.isEmpty()) {
            // Caso C: il capo team era l'unico componente: il team viene sciolto.
            utente.setTeamAttuale(null);
            teamRepository.delete(team.getId());
            return null;
        }

        // Caso B: il capo team lascia, restano altri membri: elezione casuale del nuovo capo.
        Utente nuovoCapo = membriRimasti.get(random.nextInt(membriRimasti.size()));
        team.promuoviCapoTeam(nuovoCapo);
        utente.setTeamAttuale(null);
        teamRepository.save(team);
        return nuovoCapo;
    }

    private void richiediHackathonAncoraInIscrizione(Team team) {
        Hackathon hackathon = team.getHackathonAttuale();
        if (hackathon != null && hackathon.getStato() != StatoHackathon.IN_ISCRIZIONE) {
            throw new IllegalStateException(
                    "Non è possibile lasciare il team: la gara è già iniziata");
        }
    }
}
