package application;

import domain.models.Account;
import domain.repository.AccountRepository;
import domain.repository.UtenteRepository;
import infrastructure.repository.InMemoryAccountRepository;
import infrastructure.repository.InMemoryUtenteRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EffettuaAccessoServiceTest {

    private AccountRepository accountRepository;
    private UtenteRepository utenteRepository;
    private RegistrazioneService registrazioneService;
    private EffettuaAccessoService service;

    @BeforeEach
    void setUp() {
        GestoreSessione.reset();

        accountRepository = new InMemoryAccountRepository();
        utenteRepository = new InMemoryUtenteRepository();
        registrazioneService = new RegistrazioneService(accountRepository, utenteRepository);
        service = new EffettuaAccessoService(accountRepository);

        registrazioneService.execute("Anna", "anna@test.it", "password1");
    }

    @AfterEach
    void tearDown() {
        GestoreSessione.reset();
    }

    // ====== SCENARIO PRINCIPALE ======

    @Test
    void accedeConCredenzialiCorrette() {
        Account account = service.execute("anna@test.it", "password1");

        assertNotNull(account);
        assertEquals("anna@test.it", account.getEmail());
    }

    @Test
    void dopoLAccessoLaSessioneContieneLUtente() {
        service.execute("anna@test.it", "password1");

        GestoreSessione sessione = GestoreSessione.getInstance();
        assertTrue(sessione.isAutenticato());
        assertEquals("Anna", sessione.richiediUtenteCorrente().getNome());
    }

    @Test
    void lAccessoFunzionaAnchePerEmailScritteDiversamente() {
        Account account = service.execute("  ANNA@Test.IT ", "password1");

        assertEquals("anna@test.it", account.getEmail());
    }

    // ====== ESTENSIONE 2a e 3a: STESSO MESSAGGIO DI ERRORE ======

    @Test
    void rifiutaUnaEmailNonRegistrata() {
        assertThrows(IllegalArgumentException.class,
                () -> service.execute("ignoto@test.it", "password1"));
    }

    @Test
    void rifiutaUnaPasswordErrata() {
        assertThrows(IllegalArgumentException.class,
                () -> service.execute("anna@test.it", "sbagliata1"));
    }

    @Test
    void emailInesistenteEPasswordErrataProduconoLoStessoMessaggio() {
        String messaggioEmailInesistente = assertThrows(IllegalArgumentException.class,
                () -> service.execute("ignoto@test.it", "password1")).getMessage();

        String messaggioPasswordErrata = assertThrows(IllegalArgumentException.class,
                () -> service.execute("anna@test.it", "sbagliata1")).getMessage();

        assertEquals(messaggioEmailInesistente, messaggioPasswordErrata);
    }

    @Test
    void unAccessoFallitoNonApreLaSessione() {
        assertThrows(IllegalArgumentException.class,
                () -> service.execute("anna@test.it", "sbagliata1"));

        assertFalse(GestoreSessione.getInstance().isAutenticato());
    }

    // ====== ESTENSIONE 3b: BLOCCO TEMPORANEO ======

    @Test
    void dopoCinqueTentativiFallitiLAccountEBloccato() {
        for (int i = 0; i < Account.MAX_TENTATIVI_FALLITI; i++) {
            assertThrows(IllegalArgumentException.class,
                    () -> service.execute("anna@test.it", "sbagliata1"));
        }

        // Al sesto tentativo l'account e' bloccato: cambia anche il tipo di eccezione
        assertThrows(IllegalStateException.class,
                () -> service.execute("anna@test.it", "sbagliata1"));
    }

    @Test
    void unAccountBloccatoRifiutaAncheLaPasswordCorretta() {
        for (int i = 0; i < Account.MAX_TENTATIVI_FALLITI; i++) {
            assertThrows(IllegalArgumentException.class,
                    () -> service.execute("anna@test.it", "sbagliata1"));
        }

        assertThrows(IllegalStateException.class,
                () -> service.execute("anna@test.it", "password1"));
    }

    @Test
    void unAccessoRiuscitoAzzeraITentativiFalliti() {
        for (int i = 0; i < Account.MAX_TENTATIVI_FALLITI - 1; i++) {
            assertThrows(IllegalArgumentException.class,
                    () -> service.execute("anna@test.it", "sbagliata1"));
        }

        Account account = service.execute("anna@test.it", "password1");

        assertEquals(0, account.getTentativiFalliti());
        assertNull(account.getBloccatoFino());
    }

    // ====== ESTENSIONE 4a: ACCOUNT NON ATTIVO ======

    @Test
    void rifiutaUnAccountDisattivato() {
        Account account = accountRepository.findByEmail("anna@test.it").orElseThrow();
        account.disattiva();
        accountRepository.save(account);

        assertThrows(IllegalStateException.class,
                () -> service.execute("anna@test.it", "password1"));
        assertFalse(GestoreSessione.getInstance().isAutenticato());
    }
}
