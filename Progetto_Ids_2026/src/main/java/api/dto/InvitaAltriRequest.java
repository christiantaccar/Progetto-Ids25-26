package api.dto;

import java.util.List;
import java.util.UUID;

public record InvitaAltriRequest(UUID richiedenteId, UUID teamId, List<UUID> invitatiIds) {
}