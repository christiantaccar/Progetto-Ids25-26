package application;

import domain.enums.RuoloStaff;
import domain.models.Account;
import domain.models.MembroStaff;
import domain.models.PersonaRegistrata;
import domain.models.Utente;
import domain.repository.AccountRepository;
import domain.repository.MembroStaffRepository;
import domain.repository.UtenteRepository;
import domain.security.CifratorePassword;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Caso d'uso "Effettuare registrazione".
 *
 * Al momento della registrazione si sceglie una volta per tutte se l'account
 * e' di un partecipante o di un membro dello staff: la natura della persona
 * e' fissata dalla sua classe e non e' piu' modificabile in seguito.
 *
 * Le due varianti sono metodi distinti proprio per rendere impossibile una
 * richiesta incoerente, come uno staff senza ruolo o un partecipante con uno.
 *
 * Tutte le validazioni precedono la creazione degli oggetti: se una fallisce,
 * nessuna persona e nessun Account restano nei repository.
 */
public class RegistrazioneService {

    private static final Pattern FORMATO_EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /** Lunghezza minima della password. */
    public static final int LUNGHEZZA_MINIMA_PASSWORD = 8;

    private final AccountRepository accountRepository;
    private final UtenteRepository utenteRepository;
    private final MembroStaffRepository membroStaffRepository;

    public RegistrazioneService(AccountRepository accountRepository,
                                UtenteRepository utenteRepository,
                                MembroStaffRepository membroStaffRepository) {
        this.accountRepository = Objects.requireNonNull(accountRepository);
        this.utenteRepository = Objects.requireNonNull(utenteRepository);
        this.membroStaffRepository = Objects.requireNonNull(membroStaffRepository);
    }

    /** Registra un partecipante: potra' creare team e iscriversi agli hackathon. */
    public Account registraPartecipante(String nome, String email, String password) {
        String passwordCifrata = validaECifra(nome, email, password);

        Utente utente = new Utente(nome, PersonaRegistrata.normalizzaEmail(email));
        utenteRepository.save(utente);

        return creaAccount(passwordCifrata, utente);
    }

    /**
     * Registra un membro dello staff con il ruolo indicato.
     *
     * Il ruolo dice che tipo di incarico potra' ricoprire; essere staff di uno
     * specifico hackathon dipende poi dall'assegnazione fatta dall'organizzatore.
     */
    public Account registraMembroStaff(String nome, String email, String password, RuoloStaff ruolo) {
        Objects.requireNonNull(ruolo, "Ruolo obbligatorio");
        String passwordCifrata = validaECifra(nome, email, password);

        MembroStaff membroStaff = new MembroStaff(ruolo, nome, PersonaRegistrata.normalizzaEmail(email));
        membroStaffRepository.save(membroStaff);

        return creaAccount(passwordCifrata, membroStaff);
    }

    private String validaECifra(String nome, String email, String password) {
        Objects.requireNonNull(nome, "Nome obbligatorio");
        Objects.requireNonNull(email, "Email obbligatoria");
        Objects.requireNonNull(password, "Password obbligatoria");

        validaFormatoEmail(email);

        if (accountRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Esiste gia' un account con questa email");
        }

        validaRobustezza(password);

        return CifratorePassword.cifra(password);
    }

    private Account creaAccount(String passwordCifrata, PersonaRegistrata persona) {
        Account account = new Account(passwordCifrata, persona);
        accountRepository.save(account);
        return account;
    }

    private void validaFormatoEmail(String email) {
        if (!FORMATO_EMAIL.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("Formato email non valido");
        }
    }

    /** La password deve avere una lunghezza minima e contenere lettere e cifre. */
    private void validaRobustezza(String password) {
        if (password.length() < LUNGHEZZA_MINIMA_PASSWORD) {
            throw new IllegalArgumentException(
                    "La password deve contenere almeno " + LUNGHEZZA_MINIMA_PASSWORD + " caratteri");
        }

        boolean haLettera = false;
        boolean haCifra = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) haLettera = true;
            if (Character.isDigit(c)) haCifra = true;
        }

        if (!haLettera || !haCifra) {
            throw new IllegalArgumentException("La password deve contenere almeno una lettera e una cifra");
        }
    }
}
