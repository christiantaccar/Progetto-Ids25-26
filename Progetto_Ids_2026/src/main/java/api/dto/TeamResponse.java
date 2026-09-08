package api.dto;

import java.util.List;
import java.util.UUID;

public record TeamResponse(UUID id, String nome, String capoTeamEmail, List<String> membriEmail, List<String> invitatiEsclusiEmail) {
}