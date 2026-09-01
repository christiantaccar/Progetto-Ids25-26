package application;

import domain.models.Account;
import domain.models.Utente;
import domain.security.CifratorePassword;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GestoreSessioneTest {

    private Account account;

    @BeforeEach
    void setUp() {
        GestoreSessione.reset();
        Utente utente = new Utente("Anna", "anna@test.it");
        account = new Account("anna@test.it", CifratorePassword.cifra("password1"), utente);
    }

    @AfterEach
    void tearDown() {
        GestoreSessione.reset();
    }

    // ====== SINGLETON ======

    @Test
    void getInstanceRestituisceSempreLaStessaIstanza() {
        assertSame(GestoreSessione.getInstance(), GestoreSessione.getInstance());
    }

    @Test
    void loStatoEcondivisoTraTutteLeChiamate() {
        GestoreSessione.getInstance().apriSessione(account);

        assertTrue(GestoreSessione.getInstance().isAutenticato());
    }

    // ====== CICLO DI VITA DELLA SESSIONE ======

    @Test
    void allInizioNessunoEAutenticato() {
        assertFalse(GestoreSessione.getInstance().isAutenticato());
        assertTrue(GestoreSessione.getInstance().getAccountCorrente().isEmpty());
    }

    @Test
    void apriSessioneRendeDisponibileLUtente() {
        GestoreSessione sessione = GestoreSessione.getInstance();
        sessione.apriSessione(account);

        assertEquals(account, sessione.richiediAccountCorrente());
        assertEquals("Anna", sessione.richiediUtenteCorrente().getNome());
    }

    @Test
    void chiudiSessioneRimuoveLUtente() {
        GestoreSessione sessione = GestoreSessione.getInstance();
        sessione.apriSessione(account);
        sessione.chiudiSessione();

        assertFalse(sessione.isAutenticato());
    }

    @Test
    void richiediAccountCorrenteFallisceSenzaAutenticazione() {
        assertThrows(IllegalStateException.class,
                () -> GestoreSessione.getInstance().richiediAccountCorrente());
    }

    @Test
    void apriSessioneRifiutaUnAccountNullo() {
        assertThrows(NullPointerException.class,
                () -> GestoreSessione.getInstance().apriSessione(null));
    }
}
