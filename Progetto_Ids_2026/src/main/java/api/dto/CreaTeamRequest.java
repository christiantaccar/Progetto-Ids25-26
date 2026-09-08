package api.dto;

import java.util.List;
import java.util.UUID;

public record CreaTeamRequest(UUID creatoreId, String nomeTeam, List<UUID> invitatiIds) {
}