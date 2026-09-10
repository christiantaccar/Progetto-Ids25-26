package api.dto;

import java.util.UUID;

public record VoceSottomissioneResponse(UUID teamId, String teamNome, SottomissioneResponse sottomissione) {
}