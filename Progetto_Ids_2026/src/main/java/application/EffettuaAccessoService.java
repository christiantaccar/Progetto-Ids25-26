package application;

import domain.models.Account;
import domain.repository.AccountRepository;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Caso d'uso "Effettuare accesso".
 *
 * Email inesistente e password errata producono volutamente lo stesso
 * messaggio: distinguerli rivelerebbe quali indirizzi sono registrati.
 */
public class EffettuaAccessoService {

    private static final String CREDENZIALI_NON_VALIDE = "Credenziali non valide";

    private final AccountRepository accountRepository;

    public EffettuaAccessoService(AccountRepository accountRepository) {
        this.accountRepository = Objects.requireNonNull(accountRepository);
    }

    public Account execute(String email, String password) {
        Objects.requireNonNull(email, "Email obbligatoria");
        Objects.requireNonNull(password, "Password obbligatoria");

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(CREDENZIALI_NON_VALIDE));

        LocalDateTime ora = LocalDateTime.now();

        if (account.isBloccato(ora)) {
            throw new IllegalStateException(
                    "Account temporaneamente bloccato per troppi tentativi falliti");
        }

        if (!account.verificaPassword(password)) {
            account.registraTentativoFallito(ora);
            accountRepository.save(account);
            throw new IllegalArgumentException(CREDENZIALI_NON_VALIDE);
        }

        if (!account.isAttivo()) {
            throw new IllegalStateException("Account non attivo");
        }

        account.azzeraTentativiFalliti();
        accountRepository.save(account);

        GestoreSessione.getInstance().apriSessione(account);

        return account;
    }
}
