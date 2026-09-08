package api.dto;

import java.util.UUID;

public record UtenteResponse(UUID id, String nome, String email) {
}