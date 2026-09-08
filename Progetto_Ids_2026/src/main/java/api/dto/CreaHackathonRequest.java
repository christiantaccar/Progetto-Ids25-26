package api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreaHackathonRequest(
        UUID organizzatoreId,
        UUID giudiceId,
        List<UUID> mentoriIds,
        String nome,
        String regolamento,
        String luogo,
        LocalDateTime dataInizio,
        LocalDate dataFine,
        LocalDate scadenzaIscrizioni,
        double premio,
        int maxTeam) {
}