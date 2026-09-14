Sistema di gestione di hackathon sviluppato come progetto per il corso di Ingegneria
del Software, anno accademico 2025/2026, Università di Camerino.

L'applicazione gestisce il ciclo di vita completo di un hackathon: la creazione da parte
dell'organizzazione, la formazione dei team da parte dei partecipanti, l'iscrizione alla
gara, l'invio e la valutazione delle sottomissioni, fino alla proclamazione del vincitore.
Il progetto nasce come modello di dominio in Java puro ed è stato successivamente esposto
tramite una API REST con Spring Boot.

## Funzionalità implementate

**Account e accesso**

Registrazione di partecipanti e di membri dello staff con ruolo (organizzatore, giudice,
mentore). Login con password cifrata tramite PBKDF2 e salt casuale, blocco automatico
dell'account dopo cinque tentativi falliti per quindici minuti, gestione della sessione
corrente.

**Gestione dell'hackathon**

Creazione di un hackathon con nome, regolamento, luogo, date, scadenza delle iscrizioni,
montepremi e numero massimo di team. Assegnazione dell'organizzatore, del giudice e dei
mentori. Consultazione dell'elenco degli hackathon.

**Team**

Creazione di un team con invito contestuale dei membri iniziali, invito di altri utenti
in un secondo momento, visualizzazione e accettazione degli inviti ricevuti, uscita
volontaria dal team con promozione automatica di un nuovo capo team, espulsione di un
componente da parte del capo team.

**Gara**

Iscrizione del team a un hackathon con verifica dello stato, della scadenza e del numero
massimo di componenti. Invio della sottomissione da parte del capo team. Valutazione delle
sottomissioni da parte del giudice. Consultazione delle sottomissioni da parte dello staff.

**Conclusione**

Proclamazione del team vincitore sulla base dei punteggi, con gestione esplicita del caso
di parità, in cui il giudice indica il team vincente fra i candidati a pari merito.

**Mentoring**

Proposta di una call da parte di un mentore verso un team, risposta del capo team,
notifica automatica alla controparte e registrazione dell'evento sul calendario.

**Transizioni di stato automatiche**

L'hackathon passa da IN_ISCRIZIONE a IN_CORSO, poi a IN_VALUTAZIONE e infine a CONCLUSO
in base al trascorrere del tempo, senza intervento di un attore umano.

## Funzionalità non implementate

Le seguenti sono presenti nel diagramma dei casi d'uso ma non sono state realizzate nel
codice, per scelta di scopo del progetto.

- Richieste di supporto dei team e loro consultazione da parte dei mentori
- Partecipazione effettiva alla call, che avviene su piattaforme esterne
- Segnalazione di un team e gestione delle segnalazioni
- Erogazione del montepremi e integrazione con un sistema di pagamento
- Modifica delle informazioni di un hackathon e sostituzione del giudice dopo la creazione
- Recupero delle credenziali
- Concessione di permessi da parte del gestore della piattaforma

Non è presente alcuna persistenza su database. I dati vivono in memoria per la durata
dell'esecuzione, dietro interfacce di repository che rendono la sostituzione con una
implementazione JPA una modifica circoscritta al solo livello infrastrutturale.

## Architettura

Il codice segue una architettura a livelli con le dipendenze rivolte verso il dominio.

```
api             controller REST e DTO (Spring Boot)
controller      un controller per caso d'uso
application     service applicativi, gestore della sessione, observer
domain          entita, enumerazioni, stati, interfacce di repository e di porta
infrastructure  implementazioni in memoria delle repository e dei servizi esterni
```

Il livello di dominio non dipende da nulla. I service applicativi dipendono soltanto dalle
interfacce dichiarate nel dominio, mai dalle loro implementazioni.

**Design pattern utilizzati**

- **State**, per il ciclo di vita dell'hackathon. Ogni stato è una classe che sa quali
  operazioni sono consentite e qual è lo stato successivo, al posto di catene di condizioni
  sparse nei service.
- **Observer**, per gli eventi sulle proposte di call. I service pubblicano l'evento senza
  conoscere chi lo ascolta, e aggiungere un nuovo osservatore non richiede di modificarli.
- **Singleton**, per il gestore della sessione utente.
- **Builder**, per la costruzione dei dati di un hackathon, che ha otto parametri.
- **Repository**, con le interfacce nel dominio e le implementazioni nell'infrastruttura.

## Requisiti ed esecuzione

Serve un JDK 17 o superiore. Gradle è incluso tramite wrapper.

```
./gradlew build          compila ed esegue i test
./gradlew test           esegue solo i test
./gradlew bootRun        avvia il server sulla porta 8080
```

Su Windows usare `gradlew.bat` al posto di `./gradlew`.

## API REST

Tutti gli endpoint sono sotto il prefisso `/api`.

| Metodo | Percorso | Descrizione |
|---|---|---|
| POST | `/registrazione/partecipante` | Registra un partecipante |
| POST | `/registrazione/staff` | Registra un membro dello staff |
| POST | `/login` | Autentica un account |
| GET | `/utenti` | Elenco dei partecipanti |
| GET | `/staff` | Elenco dei membri dello staff |
| POST | `/hackathon` | Crea un hackathon |
| GET | `/hackathons` | Elenco degli hackathon |
| GET | `/hackathon/{id}` | Dettaglio di un hackathon |
| POST | `/hackathon/mentori` | Assegna mentori a un hackathon |
| POST | `/hackathon/concludi` | Conclude un hackathon e proclama il vincitore |
| POST | `/team` | Crea un team |
| GET | `/teams` | Elenco dei team |
| GET | `/team/{id}` | Dettaglio di un team |
| POST | `/team/iscrivi` | Iscrive un team a un hackathon |
| POST | `/team/invita` | Invita utenti in un team |
| POST | `/team/espelli` | Espelle un componente |
| POST | `/team/lascia` | Esce da un team |
| GET | `/inviti/{utenteId}` | Inviti pendenti di un utente |
| POST | `/inviti/accetta` | Accetta un invito |

## Test

Diciannove classi di test con JUnit 5, che coprono i service applicativi e la logica di
dominio degli stati e delle sottomissioni. I casi verificano sia il percorso principale
sia le condizioni di errore: permessi mancanti, stati non validi, scadenze superate,
limiti di capienza dei team.

```
./gradlew test
```

Il report viene generato in `build/reports/tests/test/index.html`.

## Documentazione

Il modello UML si trova nella cartella `Visual Paradigm` del repository, nel file
`Progetto_Ids.vpp`. Contiene, per ciascuna delle quattro iterazioni, il diagramma dei
casi d'uso con le relative descrizioni dei flussi di eventi, i diagrammi delle classi di
analisi e di progetto, e i diagrammi di sequenza.

Il diagramma delle classi di progetto della quarta iterazione organizza le ottanta classi
in otto raggruppamenti che rispecchiano i package del codice: Controller, Service, Entity,
State, Observer, Enumeration, Repository e Servizi Esterni.

## Struttura del repository

```
Progetto_Ids_2026/     codice sorgente e test
Visual Paradigm/       modello UML
```
