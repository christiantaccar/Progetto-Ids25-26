package domain.observer;

import domain.models.CallProposta;

/**
 * Design pattern OBSERVER: interfaccia degli osservatori interessati al
 * ciclo di vita di una proposta di call.
 *
 * I metodi hanno implementazione vuota di default: ogni osservatore
 * ridefinisce solo gli eventi che lo riguardano, senza essere costretto a
 * dipendere dagli altri (ISP).
 */
public interface CallPropostaObserver {

    default void onPropostaCreata(CallProposta proposta) { }

    default void onPropostaAccettata(CallProposta proposta) { }

    default void onPropostaRifiutata(CallProposta proposta) { }
}
