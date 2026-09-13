package api;

import api.dto.CallPropostaResponse;
import api.dto.ProponiCallRequest;
import api.dto.RispondiCallRequest;
import application.ProponiCallService;
import application.RispondiCallPropostaService;
import domain.models.CallProposta;
import domain.models.MembroStaff;
import domain.models.Utente;
import domain.repository.MembroStaffRepository;
import domain.repository.UtenteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class CallRestController {

    private final ProponiCallService proponiCallService;
    private final RispondiCallPropostaService rispondiCallPropostaService;
    private final MembroStaffRepository membroStaffRepository;
    private final UtenteRepository utenteRepository;

    public CallRestController(ProponiCallService proponiCallService,
                              RispondiCallPropostaService rispondiCallPropostaService,
                              MembroStaffRepository membroStaffRepository,
                              UtenteRepository utenteRepository) {
        this.proponiCallService = proponiCallService;
        this.rispondiCallPropostaService = rispondiCallPropostaService;
        this.membroStaffRepository = membroStaffRepository;
        this.utenteRepository = utenteRepository;
    }

    @PostMapping("/call")
    public ResponseEntity<Object> proponiCall(@RequestBody ProponiCallRequest req) {
        try {
            MembroStaff mentore = membroStaffRepository.findById(req.mentoreId())
                    .orElseThrow(() -> new NoSuchElementException(req.mentoreId().toString()));

            CallProposta proposta = proponiCallService.execute(
                    mentore, req.teamId(), req.dataOra(), req.link());

            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(proposta));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mentore non trovato: " + e.getMessage());
        }
    }

    @PostMapping("/call/rispondi")
    public ResponseEntity<Object> rispondiCall(@RequestBody RispondiCallRequest req) {
        try {
            Utente capoTeam = utenteRepository.findById(req.capoTeamId())
                    .orElseThrow(() -> new NoSuchElementException(req.capoTeamId().toString()));

            CallProposta proposta = rispondiCallPropostaService.execute(
                    capoTeam, req.propostaId(), req.accetta());

            return ResponseEntity.ok(toResponse(proposta));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utente non trovato: " + e.getMessage());
        }
    }

    private CallPropostaResponse toResponse(CallProposta p) {
        return new CallPropostaResponse(
                p.getId(),
                p.getTeam().getId(),
                p.getTeam().getNome(),
                p.getMentore().getEmail(),
                p.getDataOra(),
                p.getLink(),
                p.getStato().name()
        );
    }
}