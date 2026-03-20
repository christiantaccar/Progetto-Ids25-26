package domain.models;

import java.time.LocalDate;
import java.util.Objects;

public class HackathonData {
    private final String nome;
    private final String luogo;
    private final LocalDate dataInizio;
    private final LocalDate dataFine;
    private final LocalDate scadenzaIscrizioni;
    private final double premio;
    private final int maxTeam;
    public HackathonData(String n, String l, LocalDate d_i, LocalDate d_f, LocalDate s, double p, int m) {

        // === VALIDAZIONE NULL ===
        this.nome = Objects.requireNonNull(n, "Nome obbligatorio");
        this.luogo = Objects.requireNonNull(l, "Luogo obbligatorio");
        this.dataInizio = Objects.requireNonNull(d_i, "Data inizio obbligatoria");
        this.dataFine = Objects.requireNonNull(d_f, "Data fine obbligatoria");
        this.scadenzaIscrizioni = Objects.requireNonNull(s, "Scadenza iscrizioni obbligatoria");

        // === VALIDAZIONE STRINGHE ===
        if (n.isBlank()) {
            throw new IllegalArgumentException("Il nome non può essere vuoto");
        }

        if (l.isBlank()) {
            throw new IllegalArgumentException("Il luogo non può essere vuoto");
        }

        // === VALIDAZIONE DATE ===
        if (d_f.isBefore(dataInizio)) {
            throw new IllegalArgumentException("La data di fine non può essere prima della data di inizio");
        }

        if (s.isAfter(dataInizio)) {
            throw new IllegalArgumentException("La scadenza iscrizioni deve essere prima dell'inizio");
        }

        // === VALIDAZIONE NUMERI ===
        if (m <= 0) {
            throw new IllegalArgumentException("maxTeam deve essere maggiore di 0");
        }

        if (p < 0) {
            throw new IllegalArgumentException("Il premio non può essere negativo");
        }

        this.premio = p;
        this.maxTeam = m;
    }



    public String getNome() { return nome; }
    public String getLuogo() { return luogo; }
    public LocalDate getDataInizio() { return dataInizio; }
    public LocalDate getDataFine() { return dataFine; }
    public LocalDate getScadenzaIscrizioni(){ return scadenzaIscrizioni;}
    public double getPremio() { return premio; }
    public int getMaxteam() { return maxTeam; }

    @Override
    public String toString() {
        return "HackathonData{" +
                "nome='" + nome + '\'' +
                ", luogo='" + luogo + '\'' +
                ", dataInizio=" + dataInizio +
                ", dataFine=" + dataFine +
                ", scadenzaIscrizioni=" + scadenzaIscrizioni +
                ", premio=" + premio + "€" +
                ", maxTeam=" + maxTeam +
                '}';
    }

}
