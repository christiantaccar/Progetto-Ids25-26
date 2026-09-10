package api.dto;

import java.util.UUID;

public record EspelliComponenteRequest(UUID capoId, UUID teamId, UUID componenteId) {
}