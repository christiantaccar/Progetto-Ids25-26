package domain.models;

import domain.security.CifratorePassword;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Credenziali di accesso di una PersonaRegistrata.
 *
 * L'Account custodisce la password (sempre in forma cifrata) ed e' l'unico
 * responsabile della sua verifica. L'email non e' duplicata qui: e' quella
 * della persona, unica fonte di verita'.
 */
public class Account {

    /** Tentativi falliti consecutivi oltre i quali l'account viene bloccato. */
    public static final int MAX_TENTATIVI_FALLITI = 5;

    /** Durata del blocco temporaneo dopo troppi tentativi falliti. */
    public static final Duration DURATA_BLOCCO = Duration.ofMinutes(15);

    private final UUID id;
    private final PersonaRegistrata persona;

    private String passwordCifrata;
    private boolean attivo;
    private int tentativiFalliti;
    private LocalDateTime bloccatoFino; // null se l'account non e' bloccato

    public Account(String passwordCifrata, PersonaRegistrata persona) {
        this.id = UUID.randomUUID();
        this.passwordCifrata = Objects.requireNonNull(passwordCifrata, "Password cifrata obbligatoria");
        this.persona = Objects.requireNonNull(persona, "Persona obbligatoria");

        this.attivo = true;
        this.tentativiFalliti = 0;
        this.bloccatoFino = null;
    }

    /** Verifica la password senza mai esporre quella memorizzata. */
    public boolean verificaPassword(String password) {
        Objects.requireNonNull(password, "Password obbligatoria");
        return CifratorePassword.verifica(password, passwordCifrata);
    }

    /**
     * Registra un tentativo di accesso fallito. Al raggiungimento del numero
     * massimo di tentativi consecutivi l'account viene bloccato temporaneamente.
     */
    public void registraTentativoFallito(LocalDateTime ora) {
        Objects.requireNonNull(ora, "Istante obbligatorio");

        this.tentativiFalliti++;
        if (this.tentativiFalliti >= MAX_TENTATIVI_FALLITI) {
            this.bloccatoFino = ora.plus(DURATA_BLOCCO);
        }
    }

    /** Azzera il contatore dei tentativi e rimuove l'eventuale blocco. */
    public void azzeraTentativiFalliti() {
        this.tentativiFalliti = 0;
        this.bloccatoFino = null;
    }

    public boolean isBloccato(LocalDateTime ora) {
        Objects.requireNonNull(ora, "Istante obbligatorio");
        return bloccatoFino != null && ora.isBefore(bloccatoFino);
    }

    public boolean isAttivo() {
        return attivo;
    }

    public void disattiva() {
        this.attivo = false;
    }

    public void riattiva() {
        this.attivo = true;
    }

    public UUID getId() { return id; }
    public PersonaRegistrata getPersona() { return persona; }

    /** Email di accesso: e' quella della persona, non una copia. */
    public String getEmail() { return persona.getEmail(); }

    public int getTentativiFalliti() { return tentativiFalliti; }
    public LocalDateTime getBloccatoFino() { return bloccatoFino; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account that = (Account) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
