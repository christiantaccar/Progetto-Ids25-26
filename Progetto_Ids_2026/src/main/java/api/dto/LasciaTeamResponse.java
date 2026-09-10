package api.dto;

public record LasciaTeamResponse(boolean teamSciolto, String nuovoCapoEmail, String messaggio) {
}