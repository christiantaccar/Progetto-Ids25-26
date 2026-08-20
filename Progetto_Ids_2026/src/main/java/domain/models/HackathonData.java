package domain.models;

import java.time.LocalDate;
import java.util.Objects;

public class HackathonData {
    private final String nome;
    private final String regolamento;
    private final String luogo;
    private final LocalDate dataInizio;
    private final LocalDate dataFine;
    private final LocalDate scadenzaIscrizioni;
    private final double premio;
    private final int maxTeam;

    private HackathonData(Builder b) {
        this.nome = b.nome;
        this.regolamento = b.regolamento;
        this.luogo = b.luogo;
        this.dataInizio = b.dataInizio;
        this.dataFine = b.dataFine;
        this.scadenzaIscrizioni = b.scadenzaIscrizioni;
        this.premio = b.premio;
        this.maxTeam = b.maxTeam;
    }

    public String getNome() { return nome; }
    public String getRegolamento() { return regolamento; }
    public String getLuogo() { return luogo; }
    public LocalDate getDataInizio() { return dataInizio; }
    public LocalDate getDataFine() { return dataFine; }
    public LocalDate getScadenzaIscrizioni() { return scadenzaIscrizioni; }
    public double getPremio() { return premio; }
    public int getMaxTeam() { return maxTeam; }

    @Override
    public String toString() {
        return "HackathonData{" +
                "nome='" + nome + '\'' +
                ", regolamento='" + regolamento + '\'' +
                ", luogo='" + luogo + '\'' +
                ", dataInizio=" + dataInizio +
                ", dataFine=" + dataFine +
                ", scadenzaIscrizioni=" + scadenzaIscrizioni +
                ", premio=" + premio + "€" +
                ", maxTeam=" + maxTeam +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String nome;
        private String regolamento;
        private String luogo;
        private LocalDate dataInizio;
        private LocalDate dataFine;
        private LocalDate scadenzaIscrizioni;
        private double premio;
        private int maxTeam;

        public Builder nome(String nome) {
            this.nome = nome;
            return this;
        }

        public Builder regolamento(String regolamento) {
            this.regolamento = regolamento;
            return this;
        }

        public Builder luogo(String luogo) {
            this.luogo = luogo;
            return this;
        }

        public Builder dataInizio(LocalDate dataInizio) {
            this.dataInizio = dataInizio;
            return this;
        }

        public Builder dataFine(LocalDate dataFine) {
            this.dataFine = dataFine;
            return this;
        }

        public Builder scadenzaIscrizioni(LocalDate scadenzaIscrizioni) {
            this.scadenzaIscrizioni = scadenzaIscrizioni;
            return this;
        }

        public Builder premio(double premio) {
            this.premio = premio;
            return this;
        }

        public Builder maxTeam(int maxTeam) {
            this.maxTeam = maxTeam;
            return this;
        }

        public HackathonData build() {
            // === VALIDAZIONE NULL ===
            Objects.requireNonNull(nome, "Nome obbligatorio");
            Objects.requireNonNull(regolamento, "Regolamento obbligatorio");
            Objects.requireNonNull(luogo, "Luogo obbligatorio");
            Objects.requireNonNull(dataInizio, "Data inizio obbligatoria");
            Objects.requireNonNull(dataFine, "Data fine obbligatoria");
            Objects.requireNonNull(scadenzaIscrizioni, "Scadenza iscrizioni obbligatoria");

            // === VALIDAZIONE STRINGHE ===
            if (nome.isBlank()) {
                throw new IllegalArgumentException("Il nome non può essere vuoto");
            }
            if (regolamento.isBlank()) {
                throw new IllegalArgumentException("Il regolamento non può essere vuoto");
            }
            if (luogo.isBlank()) {
                throw new IllegalArgumentException("Il luogo non può essere vuoto");
            }

            // === VALIDAZIONE DATE ===
            if (dataFine.isBefore(dataInizio)) {
                throw new IllegalArgumentException("La data di fine non può essere prima della data di inizio");
            }
            if (scadenzaIscrizioni.isAfter(dataInizio)) {
                throw new IllegalArgumentException("La scadenza iscrizioni deve essere prima dell'inizio");
            }

            // === VALIDAZIONE NUMERI ===
            if (maxTeam <= 0) {
                throw new IllegalArgumentException("maxTeam deve essere maggiore di 0");
            }
            if (premio < 0) {
                throw new IllegalArgumentException("Il premio non può essere negativo");
            }

            return new HackathonData(this);
        }
    }
}