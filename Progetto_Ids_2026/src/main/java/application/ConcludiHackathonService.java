package application;

import domain.models.Hackathon;
import domain.models.MembroStaff;
import domain.models.Team;
import domain.repository.HackathonRepository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ConcludiHackathonService {

    private final HackathonRepository hackathonRepository;
    private final ProclamaVincitoreService proclamaVincitoreService;

    public ConcludiHackathonService(HackathonRepository hackathonRepository,
                                     ProclamaVincitoreService proclamaVincitoreService) {
        this.hackathonRepository = Objects.requireNonNull(hackathonRepository);
        this.proclamaVincitoreService = Objects.requireNonNull(proclamaVincitoreService);
    }

    public static class RisultatoConclusione {
        public final Hackathon hackathon;              // null se richiedeSceltaGiudice == true
        public final boolean richiedeSceltaGiudice;
        public final List<Team> candidatiInParita;      // vuoto se non c'è pareggio

        public RisultatoConclusione(Hackathon hackathon, boolean richiedeSceltaGiudice, List<Team> candidatiInParita) {
            this.hackathon = hackathon;
            this.richiedeSceltaGiudice = richiedeSceltaGiudice;
            this.candidatiInParita = candidatiInParita;
        }
    }

  public RisultatoConclusione execute(MembroStaff richiedente, UUID hackathonId, Team sceltaGiudiceInCasoDiParita) {
    Objects.requireNonNull(richiedente, "Richiedente obbligatorio");
    Objects.requireNonNull(hackathonId, "Id hackathon obbligatorio");

    Hackathon hackathon = hackathonRepository.findById(hackathonId)
            .orElseThrow(() -> new IllegalArgumentException("Hackathon non trovato: " + hackathonId));

    if (sceltaGiudiceInCasoDiParita == null) {
        // Prima chiamata: avvia la conclusione. Deve essere l'organizzatore proprietario di QUESTO hackathon.
        if (!richiedente.equals(hackathon.getOrganizzatore())) {
            throw new IllegalArgumentException("Non autorizzato: non sei l'organizzatore di questo hackathon");
        }
    } else {
        // Seconda chiamata: risolve un pareggio. Deve essere il giudice di QUESTO hackathon.
        if (!richiedente.equals(hackathon.getGiudice())) {
            throw new IllegalArgumentException("Non autorizzato: non sei il giudice di questo hackathon");
        }
    }

        // La decisione è delegata allo stato corrente dell'hackathon (pattern State)
        if (!hackathon.puoProclamareVincitore()) {
            throw new IllegalStateException(
                    "Non è possibile concludere un hackathon in stato " + hackathon.getStato());
        }

        // Include UC-3: la determinazione del vincitore è delegata a ProclamaVincitoreService
        ProclamaVincitoreService.RisultatoProclamazione risultatoProclamazione =
                proclamaVincitoreService.execute(hackathonId, sceltaGiudiceInCasoDiParita);

        if (risultatoProclamazione.richiedeSceltaGiudice) {
            // Pareggio non ancora risolto: il flusso si interrompe qui.
            // Il chiamante dovrà richiamare execute(...) passando la scelta del Giudice.
            return new RisultatoConclusione(null, true, risultatoProclamazione.candidatiInParita);
        }

        hackathon.concludi(risultatoProclamazione.vincitore);
        hackathonRepository.save(hackathon);

        return new RisultatoConclusione(hackathon, false, List.of());
    }
}   