package application;

import domain.models.Account;
import domain.repository.AccountRepository;
import domain.repository.UtenteRepository;
import infrastructure.repository.InMemoryAccountRepository;
import infrastructure.repository.InMemoryUtenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrazioneServiceTest {

    private AccountRepository accountRepository;
    private UtenteRepository utenteRepository;
    private RegistrazioneService service;

    @BeforeEach
    void setUp() {
        accountRepository = new InMemoryAccountRepository();
        utenteRepository = new InMemoryUtenteRepository();
        service = new RegistrazioneService(accountRepository, utenteRepository);
    }

    // ====== SCENARIO PRINCIPALE ======

    @Test
    void registraUnNuovoAccount() {
        Account account = service.execute("Anna", "anna@test.it", "password1");

        assertNotNull(account);
        assertEquals("anna@test.it", account.getEmail());
        assertEquals("Anna", account.getUtente().getNome());
        assertTrue(account.isAttivo());
        assertTrue(accountRepository.existsByEmail("anna@test.it"));
    }

    @Test
    void laPasswordNonEMemorizzataInChiaro() {
        Account account = service.execute("Anna", "anna@test.it", "password1");

        assertTrue(account.verificaPassword("password1"));
        assertFalse(account.verificaPassword("password2"));
    }

    @Test
    void ancheIlProfiloUtenteVienePersistito() {
        Account account = service.execute("Anna", "anna@test.it", "password1");

        assertTrue(utenteRepository.findById(account.getUtente().getId()).isPresent());
    }

    @Test
    void lEmailVieneNormalizzata() {
        Account account = service.execute("Anna", "  Anna@TEST.it ", "password1");

        assertEquals("anna@test.it", account.getEmail());
        assertTrue(accountRepository.existsByEmail("ANNA@test.IT"));
    }

    // ====== ESTENSIONE 2a: FORMATO EMAIL NON VALIDO ======

    @Test
    void rifiutaEmailSenzaChiocciola() {
        assertThrows(IllegalArgumentException.class,
                () -> service.execute("Anna", "anna.test.it", "password1"));
    }

    @Test
    void rifiutaEmailSenzaDominio() {
        assertThrows(IllegalArgumentException.class,
                () -> service.execute("Anna", "anna@test", "password1"));
    }

    // ====== ESTENSIONE 3a: EMAIL GIA' REGISTRATA ======

    @Test
    void rifiutaUnaEmailGiaRegistrata() {
        service.execute("Anna", "anna@test.it", "password1");

        assertThrows(IllegalArgumentException.class,
                () -> service.execute("Anna Bis", "anna@test.it", "password2"));
    }

    @Test
    void ilConfrontoSullEmailEsistenteIgnoraMaiuscoleESpazi() {
        service.execute("Anna", "anna@test.it", "password1");

        assertThrows(IllegalArgumentException.class,
                () -> service.execute("Anna Bis", " ANNA@Test.IT ", "password2"));
    }

    // ====== ESTENSIONE 4a: PASSWORD NON CONFORME ======

    @Test
    void rifiutaPasswordTroppoCorta() {
        assertThrows(IllegalArgumentException.class,
                () -> service.execute("Anna", "anna@test.it", "pass1"));
    }

    @Test
    void rifiutaPasswordSenzaCifre() {
        assertThrows(IllegalArgumentException.class,
                () -> service.execute("Anna", "anna@test.it", "passwordsenzacifre"));
    }

    @Test
    void rifiutaPasswordSenzaLettere() {
        assertThrows(IllegalArgumentException.class,
                () -> service.execute("Anna", "anna@test.it", "12345678"));
    }

    // ====== NESSUNO STATO SPORCO DOPO UN ERRORE ======

    @Test
    void unaRegistrazioneFallitaNonLasciaNullaNeiRepository() {
        assertThrows(IllegalArgumentException.class,
                () -> service.execute("Anna", "anna@test.it", "corta"));

        assertFalse(accountRepository.existsByEmail("anna@test.it"));
    }

    @Test
    void nonApreAlcunaSessione() {
        GestoreSessione.reset();

        service.execute("Anna", "anna@test.it", "password1");

        assertFalse(GestoreSessione.getInstance().isAutenticato());
    }
}
