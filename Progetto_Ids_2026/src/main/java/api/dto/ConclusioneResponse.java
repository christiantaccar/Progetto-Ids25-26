package api.dto;

import java.util.List;
import java.util.UUID;

public record ConclusioneResponse(UUID hackathonId, String stato, boolean richiedeSceltaGiudice,
                                  List<String> candidatiInParita, String vincitore) {
}