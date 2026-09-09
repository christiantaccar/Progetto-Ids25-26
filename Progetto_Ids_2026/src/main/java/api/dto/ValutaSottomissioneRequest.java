package api.dto;

import java.util.UUID;

public record ValutaSottomissioneRequest(UUID giudiceId, UUID teamId, int punteggio) {
}