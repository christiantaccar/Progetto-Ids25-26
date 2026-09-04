package controller;

import application.RispondiCallPropostaService;
import domain.models.CallProposta;
import domain.models.Utente;

import java.util.Objects;
import java.util.UUID;

public class RispondiCallPropostaController {

    private final RispondiCallPropostaService service;

    public RispondiCallPropostaController(RispondiCallPropostaService service) {
        this.service = Objects.requireNonNull(service);
    }

    public CallProposta rispondiCall(Utente capoTeam, UUID propostaId, boolean accetta) {
        return service.execute(capoTeam, propostaId, accetta);
    }
}
