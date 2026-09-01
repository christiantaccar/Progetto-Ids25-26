package controller;

import application.EffettuaAccessoService;
import application.GestoreSessione;
import domain.models.Account;

import java.util.Objects;

public class EffettuaAccessoController {

    private final EffettuaAccessoService service;

    public EffettuaAccessoController(EffettuaAccessoService service) {
        this.service = Objects.requireNonNull(service);
    }

    public Account effettuaAccesso(String email, String password) {
        return service.execute(email, password);
    }

    /** Chiude la sessione dell'utente autenticato. */
    public void effettuaLogout() {
        GestoreSessione.getInstance().chiudiSessione();
    }
}
