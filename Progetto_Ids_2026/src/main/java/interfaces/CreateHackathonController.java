package interfaces;

import application.CreateHackathonService;
import domain.models.Hackathon;
import domain.models.HackathonData;
import domain.models.MembroStaff;

import java.util.Objects;

public class CreateHackathonController {

    private final CreateHackathonService service;

    public CreateHackathonController(CreateHackathonService service) {
        this.service = Objects.requireNonNull(service);
    }

    public Hackathon createHackathon(MembroStaff organizzatore,
                                     HackathonData data,
                                     MembroStaff giudice,
                                     MembroStaff mentore) {
        // Nessuna logica qui — solo delega al service
        return service.execute(organizzatore, data, giudice, mentore);
    }
}