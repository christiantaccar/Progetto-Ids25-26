package application;

import domain.models.Account;
import domain.models.Utente;

import java.util.Objects;
import java.util.Optional;

/**
 * Design pattern SINGLETON.
 *
 * Custodisce l'account autenticato nella sessione corrente. Esiste una sola
 * istanza perche' esiste una sola sessione attiva per esecuzione: i controller
 * vi accedono per sapere chi sta agendo, invece di ricevere l'identita' come
 * parametro e doversi fidare del chiamante.
 */
public class GestoreSessione {

    private static GestoreSessione istanza;

    private Account accountCorrente; // null se nessuno e' autenticato

    /** Costruttore privato: l'istanza si ottiene solo tramite getInstance(). */
    private GestoreSessione() {
    }

    public static synchronized GestoreSessione getInstance() {
        if (istanza == null) {
            istanza = new GestoreSessione();
        }
        return istanza;
    }

    public void apriSessione(Account account) {
        this.accountCorrente = Objects.requireNonNull(account, "Account obbligatorio");
    }

    public void chiudiSessione() {
        this.accountCorrente = null;
    }

    public boolean isAutenticato() {
        return accountCorrente != null;
    }

    /** Account autenticato, vuoto se nessuno ha effettuato l'accesso. */
    public Optional<Account> getAccountCorrente() {
        return Optional.ofNullable(accountCorrente);
    }

    /**
     * Account autenticato, richiesto come precondizione.
     * @throws IllegalStateException se nessun utente ha effettuato l'accesso
     */
    public Account richiediAccountCorrente() {
        if (accountCorrente == null) {
            throw new IllegalStateException("Nessun utente autenticato: effettuare l'accesso");
        }
        return accountCorrente;
    }

    /** Utente autenticato, richiesto come precondizione. */
    public Utente richiediUtenteCorrente() {
        return richiediAccountCorrente().getUtente();
    }

    /**
     * Riporta il Singleton allo stato iniziale. Serve esclusivamente ai test,
     * per garantire che ciascuno parta da una sessione pulita.
     */
    static void reset() {
        istanza = null;
    }
}
