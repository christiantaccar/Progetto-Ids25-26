package domain.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import domain.enums.StatoHackathon;


public class Team {
    private final UUID id;
    private String nome;
    private final Utente capoTeam;
    private final List<Utente> membri; // membri aggiuntivi, oltre al capo
    private Hackathon hackathonAttuale; // null se non iscritto a nessun hackathon

    public Team(String nome, Utente capoTeam) {
        this.id = UUID.randomUUID();
        this.nome = Objects.requireNonNull(nome);
        if (nome.isBlank()) {
            throw new IllegalArgumentException("Il nome del team non può essere vuoto");
        }
        this.capoTeam = Objects.requireNonNull(capoTeam);
        this.membri = new ArrayList<>();
    }

    public void aggiungiMembro(Utente utente) {
        Objects.requireNonNull(utente, "Utente non può essere null");
        if (utente.equals(capoTeam) || membri.contains(utente)) {
            throw new IllegalArgumentException("L'utente è già membro di questo team");
        }
        membri.add(utente);
    }

    public List<Utente> getTuttiIMembri() {
        List<Utente> tutti = new ArrayList<>();
        tutti.add(capoTeam);
        tutti.addAll(membri);
        return List.copyOf(tutti);
    }

    public boolean isIscrittoAdHackathonAttivo() {
        return hackathonAttuale != null && hackathonAttuale.getStato() != StatoHackathon.CONCLUSO;
    }

    public void setHackathonAttuale(Hackathon hackathon) {
        this.hackathonAttuale = hackathon;
    }


    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public Utente getCapoTeam() { return capoTeam; }
    public List<Utente> getMembri() { return List.copyOf(membri); }
    public Hackathon getHackathonAttuale() { return hackathonAttuale; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Team)) return false;
        Team that = (Team) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}