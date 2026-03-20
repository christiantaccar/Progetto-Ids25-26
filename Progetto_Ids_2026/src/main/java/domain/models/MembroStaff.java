package domain.models;

import domain.enums.RuoloStaff;

import java.util.Objects;
import java.util.UUID;

public class MembroStaff {
    private final RuoloStaff ruolo;
    private final UUID id;
    private final String nome;

    public MembroStaff(RuoloStaff r, String n){
    this.ruolo = Objects.requireNonNull(r, "Ruolo obbligatorio");;
    this.id = UUID.randomUUID();
    this.nome = Objects.requireNonNull(n, "Nome obbligatorio");


        // === VALIDAZIONE STRINGHE ===
        if (n.isBlank()) {
            throw new IllegalArgumentException("Il nome non può essere vuoto");
        }}

    public RuoloStaff getRuolo() {
        return ruolo;
    }

    public String getNome() {
        return nome;
    }

    public UUID getId() {
        return id;
    }
    public boolean isOrganizzatore(){
        return ruolo == RuoloStaff.ORGANIZZATORE;
    }
    public boolean isGiudice(){
        return ruolo == RuoloStaff.GIUDICE;
    }
    public boolean isMentore(){
        return ruolo == RuoloStaff.MENTORE;
    }
}
