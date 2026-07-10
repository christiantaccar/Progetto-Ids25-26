package controller;

import application.CreateHackathonService;
import domain.models.Hackathon;
import domain.models.HackathonData;
import domain.models.MembroStaff;

import java.util.List;
import java.util.Objects;

public class CreateHackathonController {

    private final CreateHackathonService service;

    public CreateHackathonController(CreateHackathonService service) {
        this.service = Objects.requireNonNull(service);
    }

    public Hackathon createHackathon(MembroStaff organizzatore,
                                     HackathonData data,
                                     MembroStaff giudice,
                                     List<MembroStaff> mentori) {
        return service.execute(organizzatore, data, giudice, mentori);
    }
}