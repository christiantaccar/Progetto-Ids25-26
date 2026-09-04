package application;

import domain.enums.RuoloStaff;
import domain.models.Account;
import domain.models.MembroStaff;
import domain.models.Utente;
import domain.repository.AccountRepository;
import domain.repository.MembroStaffRepository;
import domain.repository.UtenteRepository;
import infrastructure.repository.InMemoryAccountRepository;
import infrastructure.repository.InMemoryMembroStaffRepository;
import infrastructure.repository.InMemoryUtenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrazioneServiceTest {

    private AccountRepository accountRepository;
    private UtenteRepository utenteRepository;
    private MembroStaffRepository membroStaffRepository;
    private RegistrazioneService service;

    @BeforeEach
    void setUp() {
        accountRepository = new InMemoryAccountRepository();
        utenteRepository = new InMemoryUtenteRepository();
        membroStaffRepository = new InMemoryMembroStaffRepository();
        service = new RegistrazioneService(accountRepository, utenteRepository, membroStaffRepository);
    }

    // ====== SCENARIO PRINCIPALE ======

    @Test
    void registraUnNuovoAccount() {
        Account account = service.registraPartecipante("Anna", "anna@test.it", "password1");

        assertNotNull(account);
        assertEquals("anna@test.it", account.getEmail());
        assertEquals("Anna", account.getPersona().getNome());
        assertTrue(account.isAttivo());
        assertTrue(accountRepository.existsByEmail("anna@test.it"));
    }

    @Test
    void laPasswordNonEMemorizzataInChiaro() {
        Account account = service.registraPartecipante("Anna", "anna@test.it", "password1");

        assertTrue(account.verificaPassword("password1"));
        assertFalse(account.verificaPassword("password2"));
    }

    @Test
    void ancheIlProfiloUtenteVienePersistito() {
        Account account = service.registraPartecipante("Anna", "anna@test.it", "password1");

        assertTrue(utenteRepository.findById(account.getPersona().getId()).isPresent());
    }

    @Test
    void lEmailVieneNormalizzata() {
        Account account = service.registraPartecipante("Anna", "  Anna@TEST.it ", "password1");

        assertEquals("anna@test.it", account.getEmail());
        assertTrue(accountRepository.existsByEmail("ANNA@test.IT"));
    }

    // ====== ESTENSIONE 2a: FORMATO EMAIL NON VALIDO ======

    @Test
    void rifiutaEmailSenzaChiocciola() {
        assertThrows(IllegalArgumentException.class,
                () -> service.registraPartecipante("Anna", "anna.test.it", "password1"));
    }

    @Test
    void rifiutaEmailSenzaDominio() {
        assertThrows(IllegalArgumentException.class,
                () -> service.registraPartecipante("Anna", "anna@test", "password1"));
    }

    // ====== ESTENSIONE 3a: EMAIL GIA' REGISTRATA ======

    @Test
    void rifiutaUnaEmailGiaRegistrata() {
        service.registraPartecipante("Anna", "anna@test.it", "password1");

        assertThrows(IllegalArgumentException.class,
                () -> service.registraPartecipante("Anna Bis", "anna@test.it", "password2"));
    }

    @Test
    void ilConfrontoSullEmailEsistenteIgnoraMaiuscoleESpazi() {
        service.registraPartecipante("Anna", "anna@test.it", "password1");

        assertThrows(IllegalArgumentException.class,
                () -> service.registraPartecipante("Anna Bis", " ANNA@Test.IT ", "password2"));
    }

    // ====== ESTENSIONE 4a: PASSWORD NON CONFORME ======

    @Test
    void rifiutaPasswordTroppoCorta() {
        assertThrows(IllegalArgumentException.class,
                () -> service.registraPartecipante("Anna", "anna@test.it", "pass1"));
    }

    @Test
    void rifiutaPasswordSenzaCifre() {
        assertThrows(IllegalArgumentException.class,
                () -> service.registraPartecipante("Anna", "anna@test.it", "passwordsenzacifre"));
    }

    @Test
    void rifiutaPasswordSenzaLettere() {
        assertThrows(IllegalArgumentException.class,
                () -> service.registraPartecipante("Anna", "anna@test.it", "12345678"));
    }

    // ====== NESSUNO STATO SPORCO DOPO UN ERRORE ======

    @Test
    void unaRegistrazioneFallitaNonLasciaNullaNeiRepository() {
        assertThrows(IllegalArgumentException.class,
                () -> service.registraPartecipante("Anna", "anna@test.it", "corta"));

        assertFalse(accountRepository.existsByEmail("anna@test.it"));
    }

    @Test
    void nonApreAlcunaSessione() {
        GestoreSessione.reset();

        service.registraPartecipante("Anna", "anna@test.it", "password1");

        assertFalse(GestoreSessione.getInstance().isAutenticato());
    }

    // ====== REGISTRAZIONE COME MEMBRO DELLO STAFF ======

    @Test
    void registraUnMembroDelloStaffConIlSuoRuolo() {
        Account account = service.registraMembroStaff("Luigi", "luigi@test.it", "password1", RuoloStaff.GIUDICE);

        assertTrue(account.getPersona() instanceof MembroStaff);
        assertTrue(((MembroStaff) account.getPersona()).isGiudice());
    }

    @Test
    void unMembroDelloStaffNonEUnPartecipante() {
        Account account = service.registraMembroStaff("Luigi", "luigi@test.it", "password1", RuoloStaff.GIUDICE);

        assertFalse(account.getPersona() instanceof Utente);
    }

    @Test
    void unPartecipanteNonEUnMembroDelloStaff() {
        Account account = service.registraPartecipante("Anna", "anna@test.it", "password1");

        assertFalse(account.getPersona() instanceof MembroStaff);
    }

    @Test
    void ciascunoFinisceNelProprioRepository() {
        Account partecipante = service.registraPartecipante("Anna", "anna@test.it", "password1");
        Account staff = service.registraMembroStaff("Luigi", "luigi@test.it", "password1", RuoloStaff.MENTORE);

        assertTrue(utenteRepository.findById(partecipante.getPersona().getId()).isPresent());
        assertTrue(membroStaffRepository.findById(staff.getPersona().getId()).isPresent());
        assertTrue(utenteRepository.findById(staff.getPersona().getId()).isEmpty());
        assertTrue(membroStaffRepository.findById(partecipante.getPersona().getId()).isEmpty());
    }

    @Test
    void ilRuoloEObbligatorioPerLoStaff() {
        assertThrows(NullPointerException.class,
                () -> service.registraMembroStaff("Luigi", "luigi@test.it", "password1", null));
    }

    @Test
    void lEmailEUnicaFraPartecipantiEStaff() {
        service.registraPartecipante("Anna", "anna@test.it", "password1");

        assertThrows(IllegalArgumentException.class,
                () -> service.registraMembroStaff("Anna", "anna@test.it", "password1", RuoloStaff.GIUDICE));
    }

    @Test
    void unaRegistrazioneStaffFallitaNonLasciaNullaNeiRepository() {
        assertThrows(IllegalArgumentException.class,
                () -> service.registraMembroStaff("Luigi", "luigi@test.it", "corta", RuoloStaff.GIUDICE));

        assertTrue(membroStaffRepository.findAll().isEmpty());
        assertFalse(accountRepository.existsByEmail("luigi@test.it"));
    }
}
