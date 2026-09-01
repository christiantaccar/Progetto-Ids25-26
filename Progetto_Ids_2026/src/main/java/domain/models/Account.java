package domain.models;

import domain.security.CifratorePassword;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Credenziali di accesso di una persona registrata.
 *
 * L'Account custodisce la password (sempre in forma cifrata) ed e' l'unico
 * responsabile della sua verifica: nessun altro oggetto deve poterla leggere.
 */
public class Account {

    /** Tentativi falliti consecutivi oltre i quali l'account viene bloccato. */
    public static final int MAX_TENTATIVI_FALLITI = 5;

    /** Durata del blocco temporaneo dopo troppi tentativi falliti. */
    public static final Duration DURATA_BLOCCO = Duration.ofMinutes(15);

    private final UUID id;
    private final String email;
    private final Utente utente;

    private String passwordCifrata;
    private boolean attivo;
    private int tentativiFalliti;
    private LocalDateTime bloccatoFino; // null se l'account non e' bloccato

    public Account(String email, String passwordCifrata, Utente utente) {
        this.id = UUID.randomUUID();
        this.email = normalizza(Objects.requireNonNull(email, "Email obbligatoria"));
        this.passwordCifrata = Objects.requireNonNull(passwordCifrata, "Password cifrata obbligatoria");
        this.utente = Objects.requireNonNull(utente, "Utente obbligatorio");

        if (this.email.isBlank()) {
            throw new IllegalArgumentException("L'email non puo' essere vuota");
        }

        this.attivo = true;
        this.tentativiFalliti = 0;
        this.bloccatoFino = null;
    }

    /** Normalizza un'email per il confronto: senza spazi e tutta minuscola. */
    public static String normalizza(String email) {
        return email.trim().toLowerCase();
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
    public String getEmail() { return email; }
    public Utente getUtente() { return utente; }
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
