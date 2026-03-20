package interfaces;

import application.VisualizzaHackathonService;
import domain.models.Hackathon;

import java.util.List;
import java.util.Objects;

public class VisualizzaHackathonController {

    private final VisualizzaHackathonService service;

    public VisualizzaHackathonController(VisualizzaHackathonService service) {
        this.service = Objects.requireNonNull(service);
    }

    public List<Hackathon> visualizza() {
        return service.execute();
    }
}