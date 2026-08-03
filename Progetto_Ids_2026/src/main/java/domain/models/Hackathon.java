package domain.models;

import domain.enums.StatoHackathon;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Hackathon {

    private final UUID id;
    private HackathonData data;
    private StatoHackathon stato;

    private MembroStaff organizzatore;
    private MembroStaff giudice;
    private final List<MembroStaff> mentori;
    private final List<Team> teamIscritti = new ArrayList<>();

    public Hackathon(UUID id, HackathonData data) {
        this.id = Objects.requireNonNull(id);
        this.data = Objects.requireNonNull(data);
        this.stato = StatoHackathon.IN_ISCRIZIONE;
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

        this.mentori.add(mentore);
    }
//si può usare scheduler in SpringBoot
public void aggiornaStato(LocalDate oggi) {
    if (stato == StatoHackathon.CONCLUSO) {
        return; // stato finale, nessuna transizione automatica
    }
    if (!oggi.isBefore(data.getDataFine())) {
        stato = StatoHackathon.IN_VALUTAZIONE;
    } else if (!oggi.isBefore(data.getDataInizio())) {
        stato = StatoHackathon.IN_CORSO;
    } else {
        stato = StatoHackathon.IN_ISCRIZIONE;
    }
}
    public void iscriviTeam(Team team) {
        Objects.requireNonNull(team, "Team non può essere null");
        if (teamIscritti.size() >= data.getMaxteam()) {
            throw new IllegalStateException("Numero massimo di team raggiunto");
        }
        if (teamIscritti.contains(team)) {
            throw new IllegalArgumentException("Il team è già iscritto a questo hackathon");
        }
        teamIscritti.add(team);
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

    public StatoHackathon getStato() {
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

    public int getNumeroTeamIscritti() { return teamIscritti.size(); }

    public List<Team> getTeamIscritti() { return List.copyOf(teamIscritti); }
    // ========================
    // UTILITY
    // ========================

    private void checkNotNull(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Parametro nullo non consentito");
        }
    }
}