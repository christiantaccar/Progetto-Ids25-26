package org.example;

import application.CreateHackathonService;
import application.VisualizzaHackathonService;
import application.AssegnaMentoriService;
import domain.enums.RuoloStaff;
import domain.models.HackathonData;
import domain.models.MembroStaff;
import domain.models.Hackathon;
import domain.factory.HackathonFactory;
import infrastructure.repository.InMemoryHackathonRepository;
import interfaces.CreateHackathonController;
import interfaces.VisualizzaHackathonController;
import interfaces.AssegnaMentoriController;

import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        // 1. Costruisci infrastructure
        InMemoryHackathonRepository repository =
                new InMemoryHackathonRepository();
        HackathonFactory factory = new HackathonFactory();

        // 2. Costruisci services
        CreateHackathonService createService =
                new CreateHackathonService(factory, repository);
        VisualizzaHackathonService visualizzaService =
                new VisualizzaHackathonService(repository);
        AssegnaMentoriService assegnaService =
                new AssegnaMentoriService(repository);

        // 3. Costruisci controllers
        CreateHackathonController createCtrl =
                new CreateHackathonController(createService);
        VisualizzaHackathonController visualizzaCtrl =
                new VisualizzaHackathonController(visualizzaService);
        AssegnaMentoriController assegnaCtrl =
                new AssegnaMentoriController(assegnaService);

        // 4. Prova il flusso
        MembroStaff organizzatore =
                new MembroStaff(RuoloStaff.ORGANIZZATORE, "Mario Rossi");
        MembroStaff giudice=
                new MembroStaff(RuoloStaff.GIUDICE, "Mario Giudi");
        MembroStaff mentore =
                new MembroStaff(RuoloStaff.MENTORE, "Anna Verdi");
        MembroStaff mentore2 =
                new MembroStaff(RuoloStaff.MENTORE, "Valerio Virgili");

        HackathonData data = new HackathonData(
                "HackaMI 2025",
                "Milano",
                LocalDate.of(2025, 6, 1),
                LocalDate.of(2025, 6, 3),
                LocalDate.of(2025, 5, 25),
                5000.0,
                10
        );

        // Crea hackathon
        Hackathon h = createCtrl.createHackathon(organizzatore, data,giudice, mentore);
        System.out.println("Creato: " + h.getData().getNome());

        // Assegna mentore
        assegnaCtrl.assegnaMentore(organizzatore, h.getId(), mentore2);
        System.out.println("Mentore assegnato: " + mentore.getNome());

        // Visualizza lista
        List<Hackathon> lista = visualizzaCtrl.visualizza();
        System.out.println("Hackathon totali: " + lista.size());
    }
}

        /*Riepilogo flusso completo
```
Main
 │
         ├── costruisce → Repository + Factory
 ├── costruisce → Service (inietta Repository + Factory)
 └── costruisce → Controller (inietta Service)

Richiesta utente
 └── Controller → Service → Repository/Factory → Domain Model*/