package api;

import api.dto.CreaHackathonRequest;
import api.dto.HackathonResponse;
import application.CreateHackathonService;
import domain.models.Hackathon;
import domain.models.HackathonData;
import domain.models.MembroStaff;
import domain.repository.MembroStaffRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import api.dto.MembroStaffResponse;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class HackathonRestController {

    private final CreateHackathonService createHackathonService;
    private final MembroStaffRepository membroStaffRepository;

    public HackathonRestController(CreateHackathonService createHackathonService,
                                   MembroStaffRepository membroStaffRepository) {
        this.createHackathonService = createHackathonService;
        this.membroStaffRepository = membroStaffRepository;
    }

    @PostMapping("/hackathon")
    public ResponseEntity<Object> creaHackathon(@RequestBody CreaHackathonRequest req) {
        try {
            MembroStaff organizzatore = trovaStaff(req.organizzatoreId());
            MembroStaff giudice = trovaStaff(req.giudiceId());
            List<MembroStaff> mentori = req.mentoriIds().stream()
                    .map(this::trovaStaff)
                    .toList();

            HackathonData data = HackathonData.builder()
                    .nome(req.nome())
                    .regolamento(req.regolamento())
                    .luogo(req.luogo())
                    .dataInizio(req.dataInizio())
                    .dataFine(req.dataFine())
                    .scadenzaIscrizioni(req.scadenzaIscrizioni())
                    .premio(req.premio())
                    .maxTeam(req.maxTeam())
                    .build();

            Hackathon h = createHackathonService.execute(organizzatore, data, giudice, mentori);

            return ResponseEntity.status(HttpStatus.CREATED).body(new HackathonResponse(
                    h.getId(),
                    h.getData().getNome(),
                    h.getStato().name(),
                    h.getOrganizzatore().getEmail(),
                    h.getGiudice().getEmail(),
                    h.getMentori().stream().map(MembroStaff::getEmail).toList()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Membro dello staff non trovato: " + e.getMessage());
        }
    }

    private MembroStaff trovaStaff(java.util.UUID id) {
        return membroStaffRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(id.toString()));
    }
    @GetMapping("/staff")
    public ResponseEntity<Object> elencoStaff() {
        List<MembroStaffResponse> elenco = membroStaffRepository.findAll().stream()
                .map(m -> new MembroStaffResponse(m.getId(), m.getNome(), m.getEmail(), m.getRuolo().name()))
                .toList();
        return ResponseEntity.ok(elenco);
    }
}