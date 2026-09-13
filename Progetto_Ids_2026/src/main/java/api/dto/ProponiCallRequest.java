package api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProponiCallRequest(UUID mentoreId, UUID teamId, LocalDateTime dataOra, String link) {
}