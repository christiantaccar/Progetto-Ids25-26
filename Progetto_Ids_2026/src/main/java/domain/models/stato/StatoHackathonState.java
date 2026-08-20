package domain.models.stato;

import domain.enums.StatoHackathon;
import domain.models.HackathonData;

import java.time.LocalDate;

/**
 * Design Pattern STATE.
 *
 * Ogni stato del ciclo di vita di un Hackathon è rappresentato da una classe
 * concreta che sa:
 *  - quali operazioni sono consentite mentre l'hackathon si trova in quello stato;
 *  - qual è lo stato successivo al trascorrere del tempo.
 *
 * In questo modo la logica "cosa posso fare adesso" non è piu' sparsa in
 * catene di if sull'enum dentro i Service, ma vive dentro lo stato stesso.
 */
public interface StatoHackathonState {

    /** Enum corrispondente: usato per la persistenza e per l'esposizione via API. */
    StatoHackathon tipo();

    /**
     * Transizione automatica guidata dal tempo (attore "Tempo" del diagramma dei casi d'uso).
     * Restituisce lo stato in cui l'hackathon deve trovarsi alla data indicata.
     * Se non c'e' transizione da fare, restituisce se stesso.
     */
    StatoHackathonState prossimo(HackathonData data, LocalDate oggi);

    boolean puoIscrivereTeam();

    boolean puoAggiungereMentori();

    boolean puoRicevereSottomissioni();

    boolean puoValutareSottomissioni();

    boolean puoProclamareVincitore();
}
