package api.dto;

import java.util.UUID;

public record MembroStaffResponse(UUID id, String nome, String email, String ruolo) {
}