package api.dto;

import java.util.UUID;

public record AccountResponse(UUID id, String email) {
}