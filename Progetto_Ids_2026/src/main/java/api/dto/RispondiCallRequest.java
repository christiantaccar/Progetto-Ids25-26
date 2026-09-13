package api.dto;

import java.util.UUID;

public record RispondiCallRequest(UUID capoTeamId, UUID propostaId, boolean accetta) {
}