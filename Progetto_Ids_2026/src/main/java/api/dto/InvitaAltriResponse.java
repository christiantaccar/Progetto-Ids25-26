package api.dto;

import java.util.List;

public record InvitaAltriResponse(List<String> invitatiEmail, List<String> esclusiEmail) {
}