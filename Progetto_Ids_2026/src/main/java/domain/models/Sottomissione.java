package domain.models;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Sottomissione {
    private final UUID id;
    private final Team team;
    private final Hackathon hackathon;
    private final String link;
    private final LocalDate dataInvio;
    private int punteggio;
    private boolean valutata;

    public Sottomissione(Team team, Hackathon hackathon, String link) {
        this.id = UUID.randomUUID();
        this.team = Objects.requireNonNull(team, "Team obbligatorio");
        this.hackathon = Objects.requireNonNull(hackathon, "Hackathon obbligatorio");
        this.link = Objects.requireNonNull(link, "Link obbligatorio");
        if (link.isBlank()) {
            throw new IllegalArgumentException("Il link non può essere vuoto");
        }
        this.dataInvio = LocalDate.now();
        this.valutata = false;
    }

    public void valuta(int punteggio) {
        if (valutata) {
            throw new IllegalStateException("La sottomissione è già stata valutata: il voto è definitivo");
        }
        if (punteggio < 0 || punteggio > 100) {
            throw new IllegalArgumentException("Il punteggio deve essere compreso tra 0 e 100");
        }
        this.punteggio = punteggio;
        this.valutata = true;
    }

    public boolean isValutata() {
        return valutata;
    }

    public UUID getId() { return id; }
    public Team getTeam() { return team; }
    public Hackathon getHackathon() { return hackathon; }
    public String getLink() { return link; }
    public int getPunteggio() { return punteggio; }
    public LocalDate getDataInvio() { return dataInvio; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sottomissione)) return false;
        Sottomissione that = (Sottomissione) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}