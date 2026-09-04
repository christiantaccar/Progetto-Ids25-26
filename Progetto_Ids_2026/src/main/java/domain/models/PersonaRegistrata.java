package domain.models;

import java.util.Objects;
import java.util.UUID;

/**
 * Chiunque sia registrato sulla piattaforma e possa autenticarsi.
 *
 * Raccoglie cio' che accomuna un partecipante e un membro dello staff:
 * identita', nome ed email. Le sottoclassi aggiungono solo cio' che e'
 * proprio del loro ruolo, cosi' nessuna eredita comportamenti che non le
 * appartengono.
 */
public abstract class PersonaRegistrata {

    private final UUID id;
    private final String nome;
    private final String email;

    protected PersonaRegistrata(String nome, String email) {
        this.id = UUID.randomUUID();
        this.nome = Objects.requireNonNull(nome, "Nome obbligatorio");
        this.email = normalizzaEmail(Objects.requireNonNull(email, "Email obbligatoria"));

        if (this.nome.isBlank()) {
            throw new IllegalArgumentException("Il nome non puo' essere vuoto");
        }
        if (this.email.isBlank()) {
            throw new IllegalArgumentException("L'email non puo' essere vuota");
        }
    }

    /** Forma canonica di un'email per confronti e ricerche. */
    public static String normalizzaEmail(String email) {
        return email.trim().toLowerCase();
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonaRegistrata)) return false;
        PersonaRegistrata that = (PersonaRegistrata) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
