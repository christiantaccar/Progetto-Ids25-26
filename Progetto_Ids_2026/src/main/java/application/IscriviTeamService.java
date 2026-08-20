package application;

import domain.models.Hackathon;
import domain.models.Team;
import domain.models.Utente;
import domain.repository.HackathonRepository;
import domain.repository.TeamRepository;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class IscriviTeamService {

    private final HackathonRepository hackathonRepository;
    private final TeamRepository teamRepository;

    public IscriviTeamService(HackathonRepository hackathonRepository, TeamRepository teamRepository) {
        this.hackathonRepository = Objects.requireNonNull(hackathonRepository);
        this.teamRepository = Objects.requireNonNull(teamRepository);
    }

    public void execute(Utente richiedente, UUID teamId, UUID hackathonId) {
        Objects.requireNonNull(richiedente, "Richiedente obbligatorio");
        Objects.requireNonNull(teamId, "Id team obbligatorio");
        Objects.requireNonNull(hackathonId, "Id hackathon obbligatorio");

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team non trovato: " + teamId));

        if (!richiedente.equals(team.getCapoTeam())) {
            throw new IllegalArgumentException("Solo il Capo Team può iscrivere il team");
        }

        if (team.isIscrittoAdHackathonAttivo()) {
            throw new IllegalStateException("Il team è già iscritto a un altro hackathon non concluso");
        }

        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon non trovato: " + hackathonId));

        // La decisione è delegata allo stato corrente dell'hackathon (pattern State)
        if (!hackathon.puoIscrivereTeam()) {
            throw new IllegalStateException("Le iscrizioni non sono aperte per questo hackathon");
        }

        LocalDate oggi = LocalDate.now();
        if (oggi.isAfter(hackathon.getData().getScadenzaIscrizioni())) {
            throw new IllegalStateException("Le iscrizioni per questo hackathon sono scadute");
        }

        if (team.getNumComponenti() > hackathon.getData().getMaxTeam()) {
            throw new IllegalStateException("Numero limite di componenti del team superato");
        }

        hackathon.iscriviTeam(team);
        team.setHackathonAttuale(hackathon);

        hackathonRepository.save(hackathon);
        teamRepository.save(team);
    }
}