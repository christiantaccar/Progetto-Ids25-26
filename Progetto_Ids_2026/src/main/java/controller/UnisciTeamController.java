package controller;

import application.UnisciTeamService;
import domain.models.Utente;

import java.util.Objects;
import java.util.UUID;

public class UnisciTeamController {

    private final UnisciTeamService service;

    public UnisciTeamController(UnisciTeamService service) {
        this.service = Objects.requireNonNull(service);
    }

    public void accettaInvito(Utente utente, UUID invitoId) {
        service.execute(utente, invitoId);
    }
}