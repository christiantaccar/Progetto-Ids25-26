package domain.models;

import domain.enums.StatoCallProposta;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Proposta di call rivolta da un Mentore a un Team.
 *
 * Nasce PENDENTE; il Capo Team la porta a ACCETTATA o RIFIUTATA.
 * La transizione e' definitiva: una proposta gia' chiusa non torna pendente.
 */
public class CallProposta {

    private final UUID id;
    private final MembroStaff mentore;
    private final Team team;
    private final LocalDateTime dataOra;
    private final String link;

    private StatoCallProposta stato;

    public CallProposta(MembroStaff mentore, Team team, LocalDateTime dataOra, String link) {
        this.id = UUID.randomUUID();
        this.mentore = Objects.requireNonNull(mentore, "Mentore obbligatorio");
        this.team = Objects.requireNonNull(team, "Team obbligatorio");
        this.dataOra = Objects.requireNonNull(dataOra, "Data e ora obbligatorie");
        this.link = Objects.requireNonNull(link, "Link obbligatorio");

        if (link.isBlank()) {
            throw new IllegalArgumentException("Il link non puo' essere vuoto");
        }
        if (!mentore.isMentore()) {
            throw new IllegalArgumentException("Solo un mentore puo' proporre una call");
        }

        this.stato = StatoCallProposta.PENDENTE;
    }

    public void accetta() {
        richiediPendente();
        this.stato = StatoCallProposta.ACCETTATA;
    }

    public void rifiuta() {
        richiediPendente();
        this.stato = StatoCallProposta.RIFIUTATA;
    }

    public boolean isPendente() {
        return stato == StatoCallProposta.PENDENTE;
    }

    private void richiediPendente() {
        if (!isPendente()) {
            throw new IllegalStateException(
                    "La proposta non e' piu' pendente: stato attuale " + stato);
        }
    }

    public UUID getId() { return id; }
    public MembroStaff getMentore() { return mentore; }
    public Team getTeam() { return team; }
    public LocalDateTime getDataOra() { return dataOra; }
    public String getLink() { return link; }
    public StatoCallProposta getStato() { return stato; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CallProposta)) return false;
        CallProposta that = (CallProposta) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
