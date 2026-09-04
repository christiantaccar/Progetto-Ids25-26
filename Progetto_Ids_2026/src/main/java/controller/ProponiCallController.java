package controller;

import application.ProponiCallService;
import domain.models.CallProposta;
import domain.models.MembroStaff;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class ProponiCallController {

    private final ProponiCallService service;

    public ProponiCallController(ProponiCallService service) {
        this.service = Objects.requireNonNull(service);
    }

    public CallProposta proponiCall(MembroStaff mentore, UUID teamId,
                                    LocalDateTime dataOra, String link) {
        return service.execute(mentore, teamId, dataOra, link);
    }
}
