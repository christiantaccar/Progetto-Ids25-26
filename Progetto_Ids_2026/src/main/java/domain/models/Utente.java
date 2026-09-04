package domain.models;

/**
 * Partecipante alla piattaforma: puo' far parte di un team alla volta.
 */
public class Utente extends PersonaRegistrata {

    private Team teamAttuale; // null se non appartiene a nessun team

    public Utente(String nome, String email) {
        super(nome, email);
    }

    public boolean isInTeam() {
        return teamAttuale != null;
    }

    public void setTeamAttuale(Team team) {
        this.teamAttuale = team;
    }

    public Team getTeamAttuale() {
        return teamAttuale;
    }
}
