package api;

import api.dto.AccountResponse;
import api.dto.LoginRequest;
import api.dto.RegistrazionePartecipanteRequest;
import application.EffettuaAccessoService;
import application.RegistrazioneService;
import domain.models.Account;
import api.dto.MembroStaffResponse;
import api.dto.RegistrazioneStaffRequest;
import domain.enums.RuoloStaff;
import domain.models.MembroStaff;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthRestController {

    private final RegistrazioneService registrazioneService;
    private final EffettuaAccessoService effettuaAccessoService;

    public AuthRestController(RegistrazioneService registrazioneService,
                              EffettuaAccessoService effettuaAccessoService) {
        this.registrazioneService = registrazioneService;
        this.effettuaAccessoService = effettuaAccessoService;
    }

    @PostMapping("/registrazione/partecipante")
    public ResponseEntity<Object> registraPartecipante(@RequestBody RegistrazionePartecipanteRequest req) {
        try {
            Account account = registrazioneService.registraPartecipante(
                    req.nome(), req.email(), req.password());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AccountResponse(account.getId(), account.getEmail()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest req) {
        try {
            Account account = effettuaAccessoService.execute(req.email(), req.password());
            return ResponseEntity.ok(new AccountResponse(account.getId(), account.getEmail()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.LOCKED).body(e.getMessage());
        }
    }
    @PostMapping("/registrazione/staff")
    public ResponseEntity<Object> registraStaff(@RequestBody RegistrazioneStaffRequest req) {
        try {
            RuoloStaff ruolo = RuoloStaff.valueOf(req.ruolo().toUpperCase());
            Account account = registrazioneService.registraMembroStaff(
                    req.nome(), req.email(), req.password(), ruolo);
            MembroStaff staff = (MembroStaff) account.getPersona();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new MembroStaffResponse(staff.getId(), staff.getNome(), staff.getEmail(), staff.getRuolo().name()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}