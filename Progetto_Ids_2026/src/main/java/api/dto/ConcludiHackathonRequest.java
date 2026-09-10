package api.dto;

import java.util.UUID;

public record ConcludiHackathonRequest(UUID richiedenteId, UUID hackathonId, UUID teamSceltoId) {
}