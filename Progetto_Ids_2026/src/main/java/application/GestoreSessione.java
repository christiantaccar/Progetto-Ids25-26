package application;

import domain.models.Account;
import domain.models.MembroStaff;
import domain.models.PersonaRegistrata;
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
 *
 * L'istanza si ottiene con getInstance() nel solo punto di composizione
 * dell'applicazione, che poi la passa a chi ne ha bisogno: la dipendenza
 * resta esplicita e sostituibile nei test.
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

    /** Persona autenticata, qualunque sia il suo ruolo. */
    public PersonaRegistrata richiediPersonaCorrente() {
        return richiediAccountCorrente().getPersona();
    }

    /**
     * Partecipante autenticato.
     * @throws IllegalStateException se chi e' autenticato non e' un partecipante
     */
    public Utente richiediUtenteCorrente() {
        PersonaRegistrata persona = richiediPersonaCorrente();
        if (!(persona instanceof Utente)) {
            throw new IllegalStateException("Operazione riservata ai partecipanti");
        }
        return (Utente) persona;
    }

    /**
     * Membro dello staff autenticato.
     * @throws IllegalStateException se chi e' autenticato non fa parte dello staff
     */
    public MembroStaff richiediMembroStaffCorrente() {
        PersonaRegistrata persona = richiediPersonaCorrente();
        if (!(persona instanceof MembroStaff)) {
            throw new IllegalStateException("Operazione riservata al personale");
        }
        return (MembroStaff) persona;
    }

    /**
     * Riporta il Singleton allo stato iniziale. Serve esclusivamente ai test,
     * per garantire che ciascuno parta da una sessione pulita.
     */
    static void reset() {
        istanza = null;
    }
}
