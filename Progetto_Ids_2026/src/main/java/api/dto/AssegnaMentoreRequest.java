package api.dto;

import java.util.UUID;

public record AssegnaMentoreRequest(UUID organizzatoreId, UUID hackathonId, UUID mentoreId) {
}