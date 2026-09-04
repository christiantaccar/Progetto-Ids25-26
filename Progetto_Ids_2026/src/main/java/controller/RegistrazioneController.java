package controller;

import application.RegistrazioneService;
import domain.enums.RuoloStaff;
import domain.models.Account;

import java.util.Objects;

public class RegistrazioneController {

    private final RegistrazioneService service;

    public RegistrazioneController(RegistrazioneService service) {
        this.service = Objects.requireNonNull(service);
    }

    public Account registraPartecipante(String nome, String email, String password) {
        return service.registraPartecipante(nome, email, password);
    }

    public Account registraMembroStaff(String nome, String email, String password, RuoloStaff ruolo) {
        return service.registraMembroStaff(nome, email, password, ruolo);
    }
}
