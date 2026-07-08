package application;

import application.CreateHackathonService;
import domain.enums.RuoloStaff;
import domain.models.Hackathon;
import domain.models.HackathonData;
import domain.models.MembroStaff;
import domain.repository.HackathonRepository;
import infrastructure.repository.InMemoryHackathonRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreateHackathonServiceTest {

    private HackathonData datiValidi() {
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

    @Test
    void creaHackathonConDatiValidi() {
        HackathonRepository repo = new InMemoryHackathonRepository();
        CreateHackathonService service = new CreateHackathonService(repo);

        MembroStaff organizzatore = new MembroStaff(RuoloStaff.ORGANIZZATORE, "Mario");
        MembroStaff giudice = new MembroStaff(RuoloStaff.GIUDICE, "Luigi");
        MembroStaff mentore = new MembroStaff(RuoloStaff.MENTORE, "Peach");

        Hackathon h = service.execute(organizzatore, datiValidi(), giudice, List.of(mentore));

        assertNotNull(h.getId());
        assertEquals(organizzatore, h.getOrganizzatore());
        assertEquals(giudice, h.getGiudice());
        assertTrue(h.getMentori().contains(mentore));
    }

    @Test
    void nonPermetteOrganizzatoreConRuoloSbagliato() {
        HackathonRepository repo = new InMemoryHackathonRepository();
        CreateHackathonService service = new CreateHackathonService(repo);

        MembroStaff nonOrganizzatore = new MembroStaff(RuoloStaff.GIUDICE, "Mario");
        MembroStaff giudice = new MembroStaff(RuoloStaff.GIUDICE, "Luigi");
        MembroStaff mentore = new MembroStaff(RuoloStaff.MENTORE, "Peach");

        assertThrows(IllegalArgumentException.class, () ->
                service.execute(nonOrganizzatore, datiValidi(), giudice, List.of(mentore)));
    }

    @Test
    void builderRifiutaDateIncoerenti() {
        assertThrows(IllegalArgumentException.class, () ->
                HackathonData.builder()
                        .nome("Test")
                        .regolamento("Reg")
                        .luogo("Pesaro")
                        .dataInizio(LocalDate.of(2026, 9, 3))
                        .dataFine(LocalDate.of(2026, 9, 1)) // fine prima di inizio → errore
                        .scadenzaIscrizioni(LocalDate.of(2026, 8, 25))
                        .premio(500.0)
                        .maxTeam(10)
                        .build());
    }
}
