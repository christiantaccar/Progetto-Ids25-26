package api;

import api.dto.AccettaInvitoRequest;
import api.dto.CreaTeamRequest;
import api.dto.InvitoResponse;
import api.dto.TeamResponse;
import application.CreaTeamService;
import application.UnisciTeamService;
import application.VisualizzaInvitiService;
import domain.models.Team;
import domain.models.Utente;
import domain.repository.TeamRepository;
import domain.repository.UtenteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import api.dto.EspelliComponenteRequest;
import api.dto.LasciaTeamRequest;
import api.dto.LasciaTeamResponse;
import application.EspellereComponenteService;
import application.LasciareTeamService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class TeamRestController {

    private final CreaTeamService creaTeamService;
    private final VisualizzaInvitiService visualizzaInvitiService;
    private final UnisciTeamService unisciTeamService;
    private final UtenteRepository utenteRepository;
    private final TeamRepository teamRepository;

    private final EspellereComponenteService espellereComponenteService;
    private final LasciareTeamService lasciareTeamService;

public TeamRestController(CreaTeamService creaTeamService,
                          VisualizzaInvitiService visualizzaInvitiService,
                          UnisciTeamService unisciTeamService,
                          EspellereComponenteService espellereComponenteService,   
                          LasciareTeamService lasciareTeamService,                 
                          UtenteRepository utenteRepository,
                          TeamRepository teamRepository) {
    this.creaTeamService = creaTeamService;
    this.visualizzaInvitiService = visualizzaInvitiService;
    this.unisciTeamService = unisciTeamService;
    this.espellereComponenteService = espellereComponenteService;                
    this.lasciareTeamService = lasciareTeamService;                               
    this.utenteRepository = utenteRepository;
    this.teamRepository = teamRepository;
}

    @PostMapping("/team")
    public ResponseEntity<Object> creaTeam(@RequestBody CreaTeamRequest req) {
        try {
            Utente creatore = trovaUtente(req.creatoreId());
            List<Utente> invitati = req.invitatiIds() == null
                    ? List.of()
                    : req.invitatiIds().stream().map(this::trovaUtente).toList();

            CreaTeamService.RisultatoCreazione risultato =
                    creaTeamService.execute(creatore, req.nomeTeam(), invitati);
            Team team = risultato.team;

            return ResponseEntity.status(HttpStatus.CREATED).body(new TeamResponse(
                    team.getId(),
                    team.getNome(),
                    team.getCapoTeam().getEmail(),
                    team.getMembri().stream().map(Utente::getEmail).toList(),
                    risultato.esclusi.stream().map(Utente::getEmail).toList()
            ));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utente non trovato: " + e.getMessage());
        }
    }

    @GetMapping("/inviti/{utenteId}")
    public ResponseEntity<Object> vediInviti(@PathVariable UUID utenteId) {
        try {
            Utente utente = trovaUtente(utenteId);
            List<InvitoResponse> inviti = visualizzaInvitiService.execute(utente).stream()
                    .map(inv -> new InvitoResponse(inv.getId(), inv.getTeam().getId(), inv.getTeam().getNome(), inv.getStato().name()))
                    .toList();
            return ResponseEntity.ok(inviti);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utente non trovato: " + e.getMessage());
        }
    }

    @PostMapping("/inviti/accetta")
    public ResponseEntity<Object> accettaInvito(@RequestBody AccettaInvitoRequest req) {
        try {
            Utente utente = trovaUtente(req.utenteId());
            unisciTeamService.execute(utente, req.invitoId());

            Team team = utente.getTeamAttuale();
            return ResponseEntity.ok(new TeamResponse(
                    team.getId(),
                    team.getNome(),
                    team.getCapoTeam().getEmail(),
                    team.getMembri().stream().map(Utente::getEmail).toList(),
                    List.of()
            ));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utente non trovato: " + e.getMessage());
        }
    }

    private Utente trovaUtente(UUID id) {
        return utenteRepository.findById(id).orElseThrow(() -> new NoSuchElementException(id.toString()));
    }
    @GetMapping("/team/{id}")
    public ResponseEntity<Object> vediTeam(@PathVariable UUID id) {
        return teamRepository.findById(id)
                .map(team -> ResponseEntity.ok((Object) new TeamResponse(
                        team.getId(),
                        team.getNome(),
                        team.getCapoTeam().getEmail(),
                        team.getMembri().stream().map(Utente::getEmail).toList(),
                        List.of()
                )))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Team non trovato: " + id));
    }
    @GetMapping("/teams")
    public ResponseEntity<Object> elencoTeam() {
        List<TeamResponse> elenco = teamRepository.findAll().stream()
                .map(team -> new TeamResponse(
                        team.getId(),
                        team.getNome(),
                        team.getCapoTeam().getEmail(),
                        team.getMembri().stream().map(Utente::getEmail).toList(),
                        List.of()
                ))
                .toList();
        return ResponseEntity.ok(elenco);
    }
    @PostMapping("/team/espelli")
    public ResponseEntity<Object> espelliComponente(@RequestBody EspelliComponenteRequest req) {
        try {
            Utente capo = trovaUtente(req.capoId());
            Utente componente = trovaUtente(req.componenteId());
            Team team = teamRepository.findById(req.teamId())
                    .orElseThrow(() -> new NoSuchElementException(req.teamId().toString()));

            espellereComponenteService.execute(capo, team, componente);

            Team aggiornato = teamRepository.findById(req.teamId())
                    .orElseThrow(() -> new NoSuchElementException(req.teamId().toString()));
            return ResponseEntity.ok(new TeamResponse(
                    aggiornato.getId(),
                    aggiornato.getNome(),
                    aggiornato.getCapoTeam().getEmail(),
                    aggiornato.getMembri().stream().map(Utente::getEmail).toList(),
                    List.of()
            ));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Non trovato: " + e.getMessage());
        }
    }

@PostMapping("/team/lascia")
    public ResponseEntity<Object> lasciaTeam(@RequestBody LasciaTeamRequest req) {
        try {
                Utente utente = trovaUtente(req.utenteId());
                Team teamPrimaDiUscire = utente.getTeamAttuale();

                Utente nuovoCapo = lasciareTeamService.execute(utente);

                if (nuovoCapo != null) {
                        return ResponseEntity.ok(new LasciaTeamResponse(
                                false, nuovoCapo.getEmail(), "Nuovo capo team eletto"));
                }
                boolean teamSciolto = teamPrimaDiUscire != null
                        && teamRepository.findById(teamPrimaDiUscire.getId()).isEmpty();
                return ResponseEntity.ok(new LasciaTeamResponse(
                        teamSciolto, null,
                        teamSciolto ? "Team sciolto" : "Utente rimosso dal team"));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utente non trovato: " + e.getMessage());
        }
    }
}