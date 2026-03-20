package application;

import domain.models.Hackathon;
import domain.repository.HackathonRepository;

import java.util.List;
import java.util.Objects;

public class VisualizzaHackathonService {

    private final HackathonRepository repository;

    public VisualizzaHackathonService(HackathonRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public List<Hackathon> execute() {
        return repository.findAll();
    }
}