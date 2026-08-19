package controller;

import application.InvitaAltriUtentiService;
import application.InvitaMembriService;
import domain.models.Utente;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class InvitaAltriUtentiController {

    private final InvitaAltriUtentiService service;

    public InvitaAltriUtentiController(InvitaAltriUtentiService service) {
        this.service = Objects.requireNonNull(service);
    }

    public InvitaMembriService.RisultatoInviti invitaAltriUtenti(Utente richiedente, UUID teamId, List<Utente> invitati) {
        return service.execute(richiedente, teamId, invitati);
    }
}