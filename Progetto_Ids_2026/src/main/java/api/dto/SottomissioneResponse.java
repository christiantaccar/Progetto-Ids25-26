package api.dto;

import java.time.LocalDate;
import java.util.UUID;

public record SottomissioneResponse(UUID id, String link, LocalDate dataInvio, boolean valutata, int punteggio) {
    
}