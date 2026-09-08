package api.dto;

import java.util.UUID;

public record AccettaInvitoRequest(UUID utenteId, UUID invitoId) {
}