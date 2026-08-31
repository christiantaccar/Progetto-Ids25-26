package controller;

import application.ValutaSottomissioneService;
import domain.models.MembroStaff;
import domain.models.Sottomissione;

import java.util.Objects;
import java.util.UUID;

public class ValutaSottomissioneController {

    private final ValutaSottomissioneService service;

    public ValutaSottomissioneController(ValutaSottomissioneService service) {
        this.service = Objects.requireNonNull(service);
    }

    public Sottomissione valutaSottomissione(MembroStaff giudice, UUID teamId, int punteggio) {
        return service.execute(giudice, teamId, punteggio);
    }
}
