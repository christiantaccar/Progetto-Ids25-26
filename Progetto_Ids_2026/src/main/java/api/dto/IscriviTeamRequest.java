package api.dto;

import java.util.UUID;

public record IscriviTeamRequest(UUID richiedenteId, UUID teamId, UUID hackathonId) {
}