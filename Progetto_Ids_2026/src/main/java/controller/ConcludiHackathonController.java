package controller;

import application.ConcludiHackathonService;
import domain.models.MembroStaff;
import domain.models.Team;

import java.util.Objects;
import java.util.UUID;

public class ConcludiHackathonController {

    private final ConcludiHackathonService service;

    public ConcludiHackathonController(ConcludiHackathonService service) {
        this.service = Objects.requireNonNull(service);
    }

    public ConcludiHackathonService.RisultatoConclusione concludiHackathon(MembroStaff organizzatore,
                                                                            UUID hackathonId,
                                                                            Team sceltaGiudiceInCasoDiParita) {
        return service.execute(organizzatore, hackathonId, sceltaGiudiceInCasoDiParita);
    }
}