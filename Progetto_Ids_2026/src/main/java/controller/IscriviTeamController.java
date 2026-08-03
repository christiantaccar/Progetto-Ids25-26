package controller;

import application.IscriviTeamService;
import domain.models.Utente;

import java.util.Objects;
import java.util.UUID;

public class IscriviTeamController {

    private final IscriviTeamService service;

    public IscriviTeamController(IscriviTeamService service) {
        this.service = Objects.requireNonNull(service);
    }

    public void iscriviTeam(Utente richiedente, UUID teamId, UUID hackathonId) {
        service.execute(richiedente, teamId, hackathonId);
    }
}