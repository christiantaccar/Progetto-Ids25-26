package controller;

import application.InviaSottomissioneService;
import domain.models.Sottomissione;
import domain.models.Utente;

import java.util.Objects;
import java.util.UUID;

public class InviaSottomissioneController {

    private final InviaSottomissioneService service;

    public InviaSottomissioneController(InviaSottomissioneService service) {
        this.service = Objects.requireNonNull(service);
    }

    public Sottomissione inviaSottomissione(Utente richiedente, UUID teamId, String link) {
        return service.execute(richiedente, teamId, link);
    }
}