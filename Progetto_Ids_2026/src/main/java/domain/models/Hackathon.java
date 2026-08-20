package domain.models;

import domain.enums.StatoHackathon;
import domain.models.stato.StatiHackathon;
import domain.models.stato.StatoHackathonState;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Context del design pattern STATE: delega allo stato corrente sia le
 * transizioni sia la decisione su quali operazioni siano consentite.
 */
public class Hackathon {

    private final UUID id;
    private HackathonData data;
    private StatoHackathonState stato;

    private MembroStaff organizzatore;
    private MembroStaff giudice;
    private final List<MembroStaff> mentori;
    private final List<Team> teamIscritti = new ArrayList<>();

    public Hackathon(UUID id, HackathonData data) {
        this.id = Objects.requireNonNull(id);
        this.data = Objects.requireNonNull(data);
        this.stato = StatiHackathon.iniziale();
        this.mentori = new ArrayList<>();
    }

    // ========================
    // METODI DI DOMINIO
    // ========================

    public void assegnaOrganizzatore(MembroStaff organizzatore) {
        checkNotNull(organizzatore);

        if (!organizzatore.isOrganizzatore()) {
            throw new IllegalArgumentException("Il membro non è un organizzatore");
        }

        this.organizzatore = organizzatore;
    }

    public void assegnaGiudice(MembroStaff giudice) {
        checkNotNull(giudice);

        if (!giudice.isGiudice()) {
            throw new IllegalArgumentException("Il membro non è un giudice");
        }

        this.giudice = giudice;
    }

    public void aggiungiMentore(MembroStaff mentore) {
        checkNotNull(mentore);

        if (!mentore.isMentore()) {
            throw new IllegalArgumentException("Il membro non è un mentore");
        }
        if (!stato.puoAggiungereMentori()) {
            throw new IllegalStateException(
                    "Non è possibile assegnare mentori a un hackathon in stato " + stato.tipo());
        }

        this.mentori.add(mentore);
    }

    /**
     * Transizione automatica guidata dal tempo: delegata allo stato corrente.
     * Con Spring Boot potrà essere invocata da uno scheduler.
     */
    public void aggiornaStato(LocalDate oggi) {
        Objects.requireNonNull(oggi, "Data odierna obbligatoria");
        this.stato = stato.prossimo(data, oggi);
    }

    public void iscriviTeam(Team team) {
        Objects.requireNonNull(team, "Team non può essere null");
        if (!stato.puoIscrivereTeam()) {
            throw new IllegalStateException(
                    "Le iscrizioni non sono aperte: hackathon in stato " + stato.tipo());
        }

        if (teamIscritti.contains(team)) {
            throw new IllegalArgumentException("Il team è già iscritto a questo hackathon");
        }
        teamIscritti.add(team);
    }

    // ========================
    // INTERROGAZIONI SULLO STATO (delegate allo State)
    // ========================

    public boolean puoIscrivereTeam() {
        return stato.puoIscrivereTeam();
    }

    public boolean puoAggiungereMentori() {
        return stato.puoAggiungereMentori();
    }

    public boolean puoRicevereSottomissioni() {
        return stato.puoRicevereSottomissioni();
    }

    public boolean puoValutareSottomissioni() {
        return stato.puoValutareSottomissioni();
    }

    public boolean puoProclamareVincitore() {
        return stato.puoProclamareVincitore();
    }

    public boolean isConcluso() {
        return stato.tipo() == StatoHackathon.CONCLUSO;
    }

    // ========================
    // GETTER
    // ========================

    public UUID getId() {
        return id;
    }

    public HackathonData getData() {
        return data;
    }

    /** Enum dello stato corrente: usato per persistenza ed esposizione via API. */
    public StatoHackathon getStato() {
        return stato.tipo();
    }

    /** Oggetto-stato corrente (pattern State). */
    public StatoHackathonState getStatoCorrente() {
        return stato;
    }

    public MembroStaff getOrganizzatore() {
        return organizzatore;
    }

    public MembroStaff getGiudice() {
        return giudice;
    }

    public List<MembroStaff> getMentori() {
        return List.copyOf(mentori);
    }

    public int getNumeroTeamIscritti() {
        return teamIscritti.size();
    }

    public List<Team> getTeamIscritti() {
        return List.copyOf(teamIscritti);
    }

    // ========================
    // UTILITY
    // ========================

    private void checkNotNull(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Parametro nullo non consentito");
        }
    }
}
