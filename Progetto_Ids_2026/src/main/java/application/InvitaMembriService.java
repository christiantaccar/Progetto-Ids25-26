package application;

import domain.models.Invito;
import domain.models.Team;
import domain.models.Utente;
import domain.repository.InvitoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InvitaMembriService {

    private final InvitoRepository invitoRepository;

    public InvitaMembriService(InvitoRepository invitoRepository) {
        this.invitoRepository = Objects.requireNonNull(invitoRepository);
    }

    public static class RisultatoInviti {
        public final List<Invito> creati;
        public final List<Utente> esclusi;

        public RisultatoInviti(List<Invito> creati, List<Utente> esclusi) {
            this.creati = creati;
            this.esclusi = esclusi;
        }
    }

    public RisultatoInviti execute(Team team, List<Utente> invitati) {
        List<Invito> creati = new ArrayList<>();
        List<Utente> esclusi = new ArrayList<>();

        if (invitati == null) {
            return new RisultatoInviti(creati, esclusi);
        }

        for (Utente candidato : invitati) {
            if (candidato.isInTeam()) {
                esclusi.add(candidato);
            } else {
                Invito invito = new Invito(team, candidato);
                invitoRepository.save(invito);
                creati.add(invito);
            }
        }

        return new RisultatoInviti(creati, esclusi);
    }
}