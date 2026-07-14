package controller;

import application.AssegnaMentoriService;
import domain.models.MembroStaff;

import java.util.Objects;
import java.util.UUID;

public class AssegnaMentoriController {

    private final AssegnaMentoriService service;

    public AssegnaMentoriController(AssegnaMentoriService service) {
        this.service = Objects.requireNonNull(service);
    }

    public void assegnaMentore(MembroStaff organizzatore, UUID hackathonId, MembroStaff mentore) {
        service.execute(organizzatore, hackathonId, mentore);
    }
}