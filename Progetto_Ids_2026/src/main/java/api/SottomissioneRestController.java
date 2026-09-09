package api;

import api.dto.InviaSottomissioneRequest;
import api.dto.SottomissioneResponse;
import api.dto.ValutaSottomissioneRequest;
import application.InviaSottomissioneService;
import application.ValutaSottomissioneService;
import domain.models.MembroStaff;
import domain.models.Sottomissione;
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
import java.util.UUID;


@RestController
@RequestMapping("/api")
public class SottomissioneRestController {

    private final InviaSottomissioneService inviaSottomissioneService;
    private final ValutaSottomissioneService valutaSottomissioneService;   // ← nuovo
    private final UtenteRepository utenteRepository;
    private final MembroStaffRepository membroStaffRepository;             // ← nuovo

    public SottomissioneRestController(InviaSottomissioneService inviaSottomissioneService,
                                        ValutaSottomissioneService valutaSottomissioneService,   // ← nuovo
                                        UtenteRepository utenteRepository,
                                        MembroStaffRepository membroStaffRepository) {           // ← nuovo
        this.inviaSottomissioneService = inviaSottomissioneService;
        this.valutaSottomissioneService = valutaSottomissioneService;
        this.utenteRepository = utenteRepository;
        this.membroStaffRepository = membroStaffRepository;
    }

    @PostMapping("/sottomissione")
    public ResponseEntity<Object> inviaSottomissione(@RequestBody InviaSottomissioneRequest req) {
        try {
            Utente richiedente = utenteRepository.findById(req.richiedenteId())
                    .orElseThrow(() -> new NoSuchElementException(req.richiedenteId().toString()));

            Sottomissione s = inviaSottomissioneService.execute(richiedente, req.teamId(), req.link());

            return ResponseEntity.status(HttpStatus.CREATED).body(new SottomissioneResponse(
                    s.getId(), s.getLink(), s.getDataInvio(), s.isValutata(), s.getPunteggio()
            ));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utente non trovato: " + e.getMessage());
        }
    }
    @PostMapping("/sottomissione/valuta")
    public ResponseEntity<Object> valutaSottomissione(@RequestBody ValutaSottomissioneRequest req) {
        try {
            MembroStaff giudice = membroStaffRepository.findById(req.giudiceId())
                    .orElseThrow(() -> new NoSuchElementException(req.giudiceId().toString()));

            Sottomissione s = valutaSottomissioneService.execute(giudice, req.teamId(), req.punteggio());

            return ResponseEntity.ok(new SottomissioneResponse(
                    s.getId(), s.getLink(), s.getDataInvio(), s.isValutata(), s.getPunteggio()
            ));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Giudice non trovato: " + e.getMessage());
        }
    }
}