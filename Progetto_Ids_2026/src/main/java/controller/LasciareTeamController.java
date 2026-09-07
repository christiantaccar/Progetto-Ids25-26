package controller;

import application.LasciareTeamService;
import domain.models.Utente;

import java.util.Objects;

public class LasciareTeamController {

    private final LasciareTeamService service;

    public LasciareTeamController(LasciareTeamService service) {
        this.service = Objects.requireNonNull(service);
    }

    /**
     * @return il nuovo capo team eletto, se l'utente uscente era il capo
     *         e restavano altri membri; {@code null} altrimenti
     */
    public Utente lasciaTeam(Utente utente) {
        return service.execute(utente);
    }
}
