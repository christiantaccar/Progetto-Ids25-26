package domain.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class Team {
    private final UUID id;
    private String nome;
    private Utente capoTeam; // non più final: può cambiare se il capo lascia il team
    private final List<Utente> membri; // membri aggiuntivi, oltre al capo
    private Hackathon hackathonAttuale; // null se non iscritto a nessun hackathon
    private Sottomissione sottomissioneAttuale;

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

    /**
     * Rimuove un membro (non il capo team) dal team.
     * Usata sia da "Lasciare team" (il membro rimuove se stesso) sia da
     * "Espellere componente" (il capo team rimuove un altro membro).
     *
     * @throws IllegalArgumentException se l'utente è il capo team o non è membro del team
     */
    public void rimuoviMembro(Utente utente) {
        Objects.requireNonNull(utente, "Utente non può essere null");
        if (utente.equals(capoTeam)) {
            throw new IllegalArgumentException(
                    "Il capo team non può essere rimosso con rimuoviMembro: usare promuoviCapoTeam prima di farlo uscire");
        }
        if (!membri.remove(utente)) {
            throw new IllegalArgumentException("L'utente non è membro di questo team");
        }
    }

    /**
     * Promuove un membro esistente a nuovo capo team. Il capo uscente
     * dovrà essere rimosso separatamente dal chiamante (non è più capo,
     * ma va comunque tolto dal team).
     *
     * @throws IllegalArgumentException se il nuovo capo non è già membro del team
     */
    public void promuoviCapoTeam(Utente nuovoCapo) {
        Objects.requireNonNull(nuovoCapo, "Nuovo capo team non può essere null");
        if (!membri.remove(nuovoCapo)) {
            throw new IllegalArgumentException("Il nuovo capo team deve essere un membro esistente del team");
        }
        this.capoTeam = nuovoCapo;
    }

    public List<Utente> getTuttiIMembri() {
        List<Utente> tutti = new ArrayList<>();
        tutti.add(capoTeam);
        tutti.addAll(membri);
        return List.copyOf(tutti);
    }

    public boolean isIscrittoAdHackathonAttivo() {
        return hackathonAttuale != null && !hackathonAttuale.isConcluso();
    }

    public void setHackathonAttuale(Hackathon hackathon) {
        this.hackathonAttuale = hackathon;
    }

    public void setSottomissioneAttuale(Sottomissione sottomissione){
        this.sottomissioneAttuale=sottomissione;
    }
    
    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public Utente getCapoTeam() { return capoTeam; }
    public List<Utente> getMembri() { return List.copyOf(membri); }
    public Hackathon getHackathonAttuale() { return hackathonAttuale; }
    public int getNumComponenti() { return membri.size() +1; }
    public Sottomissione getSottomissioneAttuale(){ return sottomissioneAttuale; }

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
