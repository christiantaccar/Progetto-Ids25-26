package application;

import domain.enums.RuoloStaff;
import domain.enums.StatoHackathon;
import domain.models.*;
import domain.repository.HackathonRepository;
import infrastructure.repository.InMemoryHackathonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConcludiHackathonServiceTest {

    private MembroStaff organizzatore;
    private MembroStaff altroOrganizzatore;
    private MembroStaff giudice;
    private MembroStaff mentore;

    @BeforeEach
    void setUp() {
        organizzatore = new MembroStaff(RuoloStaff.ORGANIZZATORE, "Mario");
        altroOrganizzatore = new MembroStaff(RuoloStaff.ORGANIZZATORE, "Wario");
        giudice = new MembroStaff(RuoloStaff.GIUDICE, "Luigi");
        mentore = new MembroStaff(RuoloStaff.MENTORE, "Peach");
    }

    private HackathonData datiInValutazione() {
        return HackathonData.builder()
                .nome("HackHub Test")
                .regolamento("Regolamento di prova")
                .luogo("Pesaro")
                .dataInizio(LocalDate.of(2026, 9, 1))
                .dataFine(LocalDate.of(2026, 9, 3))
                .scadenzaIscrizioni(LocalDate.of(2026, 8, 25))
                .premio(500.0)
                .maxTeam(10)
                .build();
    }

    /** Clock fisso dopo la dataFine: appena l'hackathon viene ricaricato, il suo stato risulta IN_VALUTAZIONE. */
    private Clock clockDopoLaFine() {
        return Clock.fixed(
                LocalDate.of(2026, 9, 10).atStartOfDay(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault());
    }

    private Team creaTeamConSottomissione(Hackathon hackathon, String nome, int punteggio) {
        Utente capo = new Utente(nome, nome.toLowerCase() + "@test.it");
        Team team = new Team(nome, capo);
        capo.setTeamAttuale(team);

        // Il team viene iscritto direttamente sull'oggetto (ancora IN_ISCRIZIONE
        // in memoria, prima che aggiornaStato() venga invocato dal repository).
        hackathon.iscriviTeam(team);
        team.setHackathonAttuale(hackathon);

        Sottomissione s = new Sottomissione(team, hackathon, "https://github.com/" + nome + "/progetto");
        s.valuta(punteggio);
        team.setSottomissioneAttuale(s);

        return team;
    }

    @Test
    void concludeConSuccessoEProclamaVincitore() {
        HackathonRepository repo = new InMemoryHackathonRepository(clockDopoLaFine());
        CreateHackathonService createService = new CreateHackathonService(repo);
        ProclamaVincitoreService proclamaService = new ProclamaVincitoreService(repo);
        ConcludiHackathonService concludiService = new ConcludiHackathonService(repo, proclamaService);

        Hackathon h = createService.execute(organizzatore, datiInValutazione(), giudice, List.of(mentore));
        creaTeamConSottomissione(h, "TeamA", 70);
        Team teamB = creaTeamConSottomissione(h, "TeamB", 95);

        ConcludiHackathonService.RisultatoConclusione risultato =
                concludiService.execute(organizzatore, h.getId(), null);

        assertFalse(risultato.richiedeSceltaGiudice);
        assertNotNull(risultato.hackathon);
        assertEquals(StatoHackathon.CONCLUSO, risultato.hackathon.getStato());
        assertEquals(teamB, risultato.hackathon.getTeamVincitore());
    }

    @Test
    void restituisceRichiestaSceltaGiudiceInCasoDiPareggio() {
        HackathonRepository repo = new InMemoryHackathonRepository(clockDopoLaFine());
        CreateHackathonService createService = new CreateHackathonService(repo);
        ProclamaVincitoreService proclamaService = new ProclamaVincitoreService(repo);
        ConcludiHackathonService concludiService = new ConcludiHackathonService(repo, proclamaService);

        Hackathon h = createService.execute(organizzatore, datiInValutazione(), giudice, List.of(mentore));
        Team teamA = creaTeamConSottomissione(h, "TeamA", 90);
        creaTeamConSottomissione(h, "TeamB", 90);

        ConcludiHackathonService.RisultatoConclusione risultato =
                concludiService.execute(organizzatore, h.getId(), null);

        assertTrue(risultato.richiedeSceltaGiudice);
        assertNull(risultato.hackathon);
        assertEquals(2, risultato.candidatiInParita.size());

        // L'hackathon non deve essere concluso: resta in IN_VALUTAZIONE.
        Hackathon hAncora = repo.findById(h.getId()).orElseThrow();
        assertEquals(StatoHackathon.IN_VALUTAZIONE, hAncora.getStato());
        assertNull(hAncora.getTeamVincitore());

        // Richiamando con la scelta esplicita del Giudice, la conclusione va a buon fine.
        ConcludiHackathonService.RisultatoConclusione risultatoFinale =
                concludiService.execute(organizzatore, h.getId(), teamA);

        assertFalse(risultatoFinale.richiedeSceltaGiudice);
        assertEquals(teamA, risultatoFinale.hackathon.getTeamVincitore());
        assertEquals(StatoHackathon.CONCLUSO, risultatoFinale.hackathon.getStato());
    }

    @Test
    void rifiutaSeChiamanteNonOrganizzatoreProprietario() {
        HackathonRepository repo = new InMemoryHackathonRepository(clockDopoLaFine());
        CreateHackathonService createService = new CreateHackathonService(repo);
        ProclamaVincitoreService proclamaService = new ProclamaVincitoreService(repo);
        ConcludiHackathonService concludiService = new ConcludiHackathonService(repo, proclamaService);

        Hackathon h = createService.execute(organizzatore, datiInValutazione(), giudice, List.of(mentore));

        assertThrows(IllegalArgumentException.class, () ->
                concludiService.execute(altroOrganizzatore, h.getId(), null));
    }

    @Test
    void rifiutaSeHackathonNonInStatoInValutazione() {
        // Clock reale: l'hackathon appena creato con date future resta IN_ISCRIZIONE.
        HackathonRepository repo = new InMemoryHackathonRepository();
        CreateHackathonService createService = new CreateHackathonService(repo);
        ProclamaVincitoreService proclamaService = new ProclamaVincitoreService(repo);
        ConcludiHackathonService concludiService = new ConcludiHackathonService(repo, proclamaService);

        HackathonData datiFuturi = HackathonData.builder()
                .nome("HackHub Test")
                .regolamento("Regolamento di prova")
                .luogo("Pesaro")
                .dataInizio(LocalDate.now().plusDays(30))
                .dataFine(LocalDate.now().plusDays(32))
                .scadenzaIscrizioni(LocalDate.now().plusDays(20))
                .premio(500.0)
                .maxTeam(10)
                .build();

        Hackathon h = createService.execute(organizzatore, datiFuturi, giudice, List.of(mentore));

        assertThrows(IllegalStateException.class, () ->
                concludiService.execute(organizzatore, h.getId(), null));
    }

    @Test
    void rifiutaSeHackathonNonTrovato() {
        HackathonRepository repo = new InMemoryHackathonRepository();
        ProclamaVincitoreService proclamaService = new ProclamaVincitoreService(repo);
        ConcludiHackathonService concludiService = new ConcludiHackathonService(repo, proclamaService);

        assertThrows(IllegalArgumentException.class, () ->
                concludiService.execute(organizzatore, UUID.randomUUID(), null));
    }
}