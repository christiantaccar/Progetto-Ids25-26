package controller;

import application.VisualizzaSottomissioniService;
import domain.models.MembroStaff;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class VisualizzaSottomissioniController {

    private final VisualizzaSottomissioniService service;

    public VisualizzaSottomissioniController(VisualizzaSottomissioniService service) {
        this.service = Objects.requireNonNull(service);
    }

    public List<VisualizzaSottomissioniService.VoceSottomissione> visualizzaSottomissioni(
            MembroStaff richiedente, UUID hackathonId) {
        return service.execute(richiedente, hackathonId);
    }
}