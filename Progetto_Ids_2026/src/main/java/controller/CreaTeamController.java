package controller;

import application.CreaTeamService;
import domain.models.Utente;

import java.util.List;
import java.util.Objects;

public class CreaTeamController {

    private final CreaTeamService service;

    public CreaTeamController(CreaTeamService service) {
        this.service = Objects.requireNonNull(service);
    }

    public CreaTeamService.RisultatoCreazione creaTeam(Utente creatore, String nomeTeam, List<Utente> invitati) {
        return service.execute(creatore, nomeTeam, invitati);
    }
}