package application;

import domain.models.Account;
import domain.models.Utente;
import domain.repository.AccountRepository;
import domain.repository.UtenteRepository;
import domain.security.CifratorePassword;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Caso d'uso "Effettuare registrazione".
 *
 * Tutte le validazioni precedono la creazione degli oggetti: se una fallisce,
 * nessun Utente e nessun Account restano nei repository.
 */
public class RegistrazioneService {

    private static final Pattern FORMATO_EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /** Lunghezza minima della password. */
    public static final int LUNGHEZZA_MINIMA_PASSWORD = 8;

    private final AccountRepository accountRepository;
    private final UtenteRepository utenteRepository;

    public RegistrazioneService(AccountRepository accountRepository, UtenteRepository utenteRepository) {
        this.accountRepository = Objects.requireNonNull(accountRepository);
        this.utenteRepository = Objects.requireNonNull(utenteRepository);
    }

    public Account execute(String nome, String email, String password) {
        Objects.requireNonNull(nome, "Nome obbligatorio");
        Objects.requireNonNull(email, "Email obbligatoria");
        Objects.requireNonNull(password, "Password obbligatoria");

        validaFormatoEmail(email);

        if (accountRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Esiste gia' un account con questa email");
        }

        validaRobustezza(password);

        String passwordCifrata = CifratorePassword.cifra(password);

        Utente utente = new Utente(nome, Account.normalizza(email));
        utenteRepository.save(utente);

        Account account = new Account(email, passwordCifrata, utente);
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
