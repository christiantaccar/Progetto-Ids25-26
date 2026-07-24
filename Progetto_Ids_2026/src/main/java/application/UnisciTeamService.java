package application;

import domain.models.Invito;
import domain.models.Team;
import domain.models.Utente;
import domain.repository.InvitoRepository;
import domain.repository.TeamRepository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class UnisciTeamService {

    private final InvitoRepository invitoRepository;
    private final TeamRepository teamRepository;

    public UnisciTeamService(InvitoRepository invitoRepository, TeamRepository teamRepository) {
        this.invitoRepository = Objects.requireNonNull(invitoRepository);
        this.teamRepository = Objects.requireNonNull(teamRepository);
    }

    public void execute(Utente utente, UUID invitoId) {
        Objects.requireNonNull(utente, "Utente obbligatorio");
        Objects.requireNonNull(invitoId, "Id invito obbligatorio");

        Invito invito = invitoRepository.findById(invitoId)
                .orElseThrow(() -> new IllegalArgumentException("Invito non trovato: " + invitoId));

        if (!invito.isPendente()) {
            throw new IllegalStateException("L'invito non è più valido");
        }

        if (!invito.getDestinatario().equals(utente)) {
            throw new IllegalArgumentException("L'invito non è indirizzato a questo utente");
        }

        if (utente.isInTeam()) {
            throw new IllegalStateException("L'utente è già membro di un team");
        }

        Team team = invito.getTeam();
        invito.accetta();
        team.aggiungiMembro(utente);
        utente.setTeamAttuale(team);

        List<Invito> altriPendenti = invitoRepository.findPendentiPerUtente(utente);
        for (Invito altro : altriPendenti) {
            altro.rifiuta();
            invitoRepository.save(altro);
        }

        teamRepository.save(team);
        invitoRepository.save(invito);
    }
}