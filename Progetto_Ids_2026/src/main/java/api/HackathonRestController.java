package api;

import api.dto.CreaHackathonRequest;
import api.dto.HackathonResponse;
import api.dto.IscriviTeamRequest;
import api.dto.MembroStaffResponse;
import application.CreateHackathonService;
import application.IscriviTeamService;
import application.VisualizzaHackathonService;
import domain.models.Hackathon;
import domain.models.MembroStaff;
import domain.models.Team;
import domain.models.Utente;
import domain.repository.HackathonRepository;
import domain.repository.MembroStaffRepository;
import domain.repository.UtenteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import api.dto.ConcludiHackathonRequest;
import api.dto.ConclusioneResponse;
import application.ConcludiHackathonService;
import domain.repository.TeamRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class HackathonRestController {

    private final CreateHackathonService createHackathonService;
    private final IscriviTeamService iscriviTeamService;
    private final ConcludiHackathonService concludiHackathonService;   // ← nuovo
    private final MembroStaffRepository membroStaffRepository;
    private final HackathonRepository hackathonRepository;
    private final UtenteRepository utenteRepository;
    private final TeamRepository teamRepository;                       // ← nuovo
    private final VisualizzaHackathonService visualizzaHackathonService;

    public HackathonRestController(CreateHackathonService createHackathonService,
                                   IscriviTeamService iscriviTeamService,
                                   ConcludiHackathonService concludiHackathonService,
                                   VisualizzaHackathonService visualizzaHackathonService,
                                   MembroStaffRepository membroStaffRepository,
                                   HackathonRepository hackathonRepository,
                                   UtenteRepository utenteRepository,
                                   TeamRepository teamRepository) {
        this.createHackathonService = createHackathonService;
        this.iscriviTeamService = iscriviTeamService;
        this.concludiHackathonService = concludiHackathonService;
        this.visualizzaHackathonService = visualizzaHackathonService;
        this.membroStaffRepository = membroStaffRepository;
        this.hackathonRepository = hackathonRepository;
        this.utenteRepository = utenteRepository;
        this.teamRepository = teamRepository;
    }

    @PostMapping("/hackathon")
    public ResponseEntity<Object> creaHackathon(@RequestBody CreaHackathonRequest req) {
        try {
            MembroStaff organizzatore = trovaStaff(req.organizzatoreId());
            MembroStaff giudice = trovaStaff(req.giudiceId());
            List<MembroStaff> mentori = req.mentoriIds().stream()
                    .map(this::trovaStaff)
                    .toList();

            domain.models.HackathonData data = domain.models.HackathonData.builder()
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

            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(h));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Membro dello staff non trovato: " + e.getMessage());
        }
    }

    @GetMapping("/hackathon/{id}")
    public ResponseEntity<Object> vediHackathon(@PathVariable UUID id) {
        return hackathonRepository.findById(id)
                .map(h -> ResponseEntity.ok((Object) toResponse(h)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Hackathon non trovato: " + id));
    }

    @PostMapping("/team/iscrivi")
    public ResponseEntity<Object> iscriviTeam(@RequestBody IscriviTeamRequest req) {
        try {
            Utente richiedente = utenteRepository.findById(req.richiedenteId())
                    .orElseThrow(() -> new NoSuchElementException(req.richiedenteId().toString()));
            iscriviTeamService.execute(richiedente, req.teamId(), req.hackathonId());
            return ResponseEntity.ok("Team iscritto con successo");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utente non trovato: " + e.getMessage());
        }
    }

    @GetMapping("/staff")
    public ResponseEntity<Object> elencoStaff() {
        List<MembroStaffResponse> elenco = membroStaffRepository.findAll().stream()
                .map(m -> new MembroStaffResponse(m.getId(), m.getNome(), m.getEmail(), m.getRuolo().name()))
                .toList();
        return ResponseEntity.ok(elenco);
    }

    private MembroStaff trovaStaff(UUID id) {
        return membroStaffRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(id.toString()));
    }

    private HackathonResponse toResponse(Hackathon h) {
        return new HackathonResponse(
                h.getId(),
                h.getData().getNome(),
                h.getStato().name(),
                h.getOrganizzatore().getEmail(),
                h.getGiudice().getEmail(),
                h.getMentori().stream().map(MembroStaff::getEmail).toList()
        );
    }
    @GetMapping("/hackathons")
    public ResponseEntity<Object> elencoHackathon() {
        List<HackathonResponse> elenco = visualizzaHackathonService.execute().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(elenco);
    }
    @PostMapping("/hackathon/concludi")
    public ResponseEntity<Object> concludiHackathon(@RequestBody ConcludiHackathonRequest req) {
        try {
            MembroStaff richiedente = membroStaffRepository.findById(req.richiedenteId())
                    .orElseThrow(() -> new NoSuchElementException(req.richiedenteId().toString()));

            Team teamScelto = null;
            if (req.teamSceltoId() != null) {
                teamScelto = teamRepository.findById(req.teamSceltoId())
                        .orElseThrow(() -> new NoSuchElementException(req.teamSceltoId().toString()));
            }

            ConcludiHackathonService.RisultatoConclusione risultato =
                    concludiHackathonService.execute(richiedente, req.hackathonId(), teamScelto);

            if (risultato.richiedeSceltaGiudice) {
                return ResponseEntity.ok(new ConclusioneResponse(
                        req.hackathonId(), null, true,
                        risultato.candidatiInParita.stream().map(Team::getNome).toList(),
                        null
                ));
            }

            return ResponseEntity.ok(new ConclusioneResponse(
                    risultato.hackathon.getId(),
                    risultato.hackathon.getStato().name(),
                    false,
                    List.of(),
                    risultato.hackathon.getTeamVincitore().getNome()
            ));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Non trovato: " + e.getMessage());
        }
    }
}