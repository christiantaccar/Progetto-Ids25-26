package controller;

import application.VisualizzaInvitiService;
import domain.models.Invito;
import domain.models.Utente;

import java.util.List;
import java.util.Objects;

public class VisualizzaInvitiController {

    private final VisualizzaInvitiService service;

    public VisualizzaInvitiController(VisualizzaInvitiService service) {
        this.service = Objects.requireNonNull(service);
    }

    public List<Invito> visualizzaInviti(Utente utente) {
        return service.execute(utente);
    }
}