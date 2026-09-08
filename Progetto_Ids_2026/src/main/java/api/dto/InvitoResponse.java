package api.dto;

import java.util.UUID;

public record InvitoResponse(UUID id, UUID teamId, String teamNome, String stato) {
}