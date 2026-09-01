package controller;

import application.RegistrazioneService;
import domain.models.Account;

import java.util.Objects;

public class RegistrazioneController {

    private final RegistrazioneService service;

    public RegistrazioneController(RegistrazioneService service) {
        this.service = Objects.requireNonNull(service);
    }

    public Account registra(String nome, String email, String password) {
        return service.execute(nome, email, password);
    }
}
