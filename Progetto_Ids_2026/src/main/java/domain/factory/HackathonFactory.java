package domain.factory;

import domain.models.Hackathon;
import domain.models.HackathonData;

import java.util.UUID;

public class HackathonFactory {
    public Hackathon create(HackathonData data) {
        if(data == null) throw new IllegalArgumentException("Data non validi");
        if (data.getDataInizio().isAfter(data.getDataFine())) {
            throw new IllegalArgumentException("Date non valide");
        }
        return new Hackathon(UUID.randomUUID(), data);

    }

}
