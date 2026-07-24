package domain.models;

import domain.enums.StatoInvito;

import java.util.Objects;
import java.util.UUID;

public class Invito {
    private final UUID id;
    private final Team team;
    private final Utente destinatario;
    private StatoInvito stato;

    public Invito(Team team, Utente destinatario) {
        this.id = UUID.randomUUID();
        this.team = Objects.requireNonNull(team);
        this.destinatario = Objects.requireNonNull(destinatario);
        this.stato = StatoInvito.PENDENTE;
    }

    public void accetta() {
        if (stato != StatoInvito.PENDENTE) {
            throw new IllegalStateException("L'invito non è più pendente");
        }
        this.stato = StatoInvito.ACCETTATO;
    }

    public void rifiuta() {
        if (stato != StatoInvito.PENDENTE) {
            throw new IllegalStateException("L'invito non è più pendente");
        }
        this.stato = StatoInvito.RIFIUTATO;
    }

    public boolean isPendente() {
        return stato == StatoInvito.PENDENTE;
    }

    public UUID getId() { return id; }
    public Team getTeam() { return team; }
    public Utente getDestinatario() { return destinatario; }
    public StatoInvito getStato() { return stato; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Invito)) return false;
        Invito that = (Invito) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}