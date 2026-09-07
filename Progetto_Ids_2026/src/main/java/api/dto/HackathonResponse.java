package api.dto;

import java.util.List;
import java.util.UUID;

public record HackathonResponse(
        UUID id,
        String nome,
        String stato,
        String organizzatoreEmail,
        String giudiceEmail,
        List<String> mentoriEmail) {
}