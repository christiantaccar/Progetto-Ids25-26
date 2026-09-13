package api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CallPropostaResponse(UUID id, UUID teamId, String teamNome, String mentoreEmail,
                                   LocalDateTime dataOra, String link, String stato) {
}