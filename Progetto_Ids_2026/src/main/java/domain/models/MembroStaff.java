package domain.models;

import domain.enums.RuoloStaff;

import java.util.Objects;

/**
 * Membro dello staff di un hackathon: organizzatore, giudice o mentore.
 *
 * Non estende Utente perche' non partecipa ai team: entrambi sono
 * PersonaRegistrata, ma solo l'Utente ha un'appartenenza a un team.
 */
public class MembroStaff extends PersonaRegistrata {

    private final RuoloStaff ruolo;

    public MembroStaff(RuoloStaff ruolo, String nome, String email) {
        super(nome, email);
        this.ruolo = Objects.requireNonNull(ruolo, "Ruolo obbligatorio");
    }

    public RuoloStaff getRuolo() {
        return ruolo;
    }

    public boolean isOrganizzatore() {
        return ruolo == RuoloStaff.ORGANIZZATORE;
    }

    public boolean isGiudice() {
        return ruolo == RuoloStaff.GIUDICE;
    }

    public boolean isMentore() {
        return ruolo == RuoloStaff.MENTORE;
    }
}
