package api.dto;

import java.util.UUID;

public record InviaSottomissioneRequest(UUID richiedenteId, UUID teamId, String link) {
}
