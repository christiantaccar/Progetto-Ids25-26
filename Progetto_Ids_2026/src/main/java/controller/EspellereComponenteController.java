package controller;

import application.EspellereComponenteService;
import domain.models.Team;
import domain.models.Utente;

import java.util.Objects;

public class EspellereComponenteController {

    private final EspellereComponenteService service;

    public EspellereComponenteController(EspellereComponenteService service) {
        this.service = Objects.requireNonNull(service);
    }

    public void espelliComponente(Utente capo, Team team, Utente componente) {
        service.execute(capo, team, componente);
    }
}
