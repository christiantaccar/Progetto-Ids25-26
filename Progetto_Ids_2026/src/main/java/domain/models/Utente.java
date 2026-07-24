package domain.models;

import java.util.Objects;
import java.util.UUID;

public class Utente {
    private final UUID id;
    private final String nome;
    private final String email;
    private Team teamAttuale; // null se non appartiene a nessun team

    public Utente(String nome, String email) {
        this.id = UUID.randomUUID();
        this.nome = Objects.requireNonNull(nome);
        this.email = Objects.requireNonNull(email);
    }

    public boolean isInTeam() {
        return teamAttuale != null;
    }

    public void setTeamAttuale(Team team) {
        this.teamAttuale = team;
    }

    public Team getTeamAttuale() {
        return teamAttuale;
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utente)) return false;
        Utente that = (Utente) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
