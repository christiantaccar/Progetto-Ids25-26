package api;

import application.*;
import application.observer.CalendarioObserver;
import application.observer.NotificaControparteObserver;
import domain.port.CalendarioService;
import domain.port.ServizioNotifiche;
import domain.repository.*;
import infrastructure.calendar.CalendarioSimulato;
import infrastructure.notifica.NotificheInMemory;
import infrastructure.repository.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AppConfig {

    @Bean
    public AccountRepository accountRepository() {
        return new InMemoryAccountRepository();
    }

    @Bean
    public UtenteRepository utenteRepository() {
        return new InMemoryUtenteRepository();
    }

    @Bean
    public MembroStaffRepository membroStaffRepository() {
        return new InMemoryMembroStaffRepository();
    }

    @Bean
    public RegistrazioneService registrazioneService(AccountRepository accountRepository,
                                                     UtenteRepository utenteRepository,
                                                     MembroStaffRepository membroStaffRepository) {
        return new RegistrazioneService(accountRepository, utenteRepository, membroStaffRepository);
    }

    @Bean
    public EffettuaAccessoService effettuaAccessoService(AccountRepository accountRepository) {
        return new EffettuaAccessoService(accountRepository);
    }
    @Bean
    public HackathonRepository hackathonRepository() {
        return new InMemoryHackathonRepository();
    }

    @Bean
    public CreateHackathonService createHackathonService(HackathonRepository hackathonRepository) {
        return new CreateHackathonService(hackathonRepository);
    }
    @Bean
    public TeamRepository teamRepository() {
        return new InMemoryTeamRepository();
    }

    @Bean
    public InvitoRepository invitoRepository() {
        return new InMemoryInvitoRepository();
    }

    @Bean
    public InvitaMembriService invitaMembriService(InvitoRepository invitoRepository) {
        return new InvitaMembriService(invitoRepository);
    }

    @Bean
    public CreaTeamService creaTeamService(TeamRepository teamRepository, InvitaMembriService invitaMembriService) {
        return new CreaTeamService(teamRepository, invitaMembriService);
    }
    @Bean
    public VisualizzaInvitiService visualizzaInvitiService(InvitoRepository invitoRepository) {
        return new VisualizzaInvitiService(invitoRepository);
    }

    @Bean
    public UnisciTeamService unisciTeamService(InvitoRepository invitoRepository, TeamRepository teamRepository) {
        return new UnisciTeamService(invitoRepository, teamRepository);
    }
    @Bean
    public IscriviTeamService iscriviTeamService(HackathonRepository hackathonRepository, TeamRepository teamRepository) {
        return new IscriviTeamService(hackathonRepository, teamRepository);
    }
    @Bean
    public SottomissioneRepository sottomissioneRepository() {
        return new InMemorySottomissioneRepository();
    }

    @Bean
    public InviaSottomissioneService inviaSottomissioneService(TeamRepository teamRepository,
                                                                 SottomissioneRepository sottomissioneRepository) {
        return new InviaSottomissioneService(teamRepository, sottomissioneRepository);
    }
    @Bean
    public ValutaSottomissioneService valutaSottomissioneService(TeamRepository teamRepository) {
        return new ValutaSottomissioneService(teamRepository);
    }
    @Bean
    public ProclamaVincitoreService proclamaVincitoreService(HackathonRepository hackathonRepository) {
        return new ProclamaVincitoreService(hackathonRepository);
    }

    @Bean
    public ConcludiHackathonService concludiHackathonService(HackathonRepository hackathonRepository,
                                                             ProclamaVincitoreService proclamaVincitoreService) {
        return new ConcludiHackathonService(hackathonRepository, proclamaVincitoreService);
    }
    @Bean
    public VisualizzaHackathonService visualizzaHackathonService(HackathonRepository hackathonRepository) {
        return new VisualizzaHackathonService(hackathonRepository);
    }

    @Bean
    public VisualizzaSottomissioniService visualizzaSottomissioniService(HackathonRepository hackathonRepository) {
        return new VisualizzaSottomissioniService(hackathonRepository);
    }
    @Bean
    public AssegnaMentoriService assegnaMentoriService(HackathonRepository hackathonRepository) {
    return new AssegnaMentoriService(hackathonRepository);
    }
    @Bean
    public EspellereComponenteService espellereComponenteService(TeamRepository teamRepository) {
        return new EspellereComponenteService(teamRepository);
    }

    @Bean
    public LasciareTeamService lasciareTeamService(TeamRepository teamRepository) {
        return new LasciareTeamService(teamRepository);
    }

    @Bean
    public InvitaAltriUtentiService invitaAltriUtentiService(TeamRepository teamRepository,
                                                             InvitaMembriService invitaMembriService) {
        return new InvitaAltriUtentiService(teamRepository, invitaMembriService);
    }
    @Bean
    public CallPropostaRepository callPropostaRepository() {
        return new InMemoryCallPropostaRepository();
    }

    @Bean
    public CalendarioService calendarioService() {
        return new CalendarioSimulato();
    }

    @Bean
    public ServizioNotifiche servizioNotifiche() {
        return new NotificheInMemory();
    }

    @Bean
    public GestoreNotificheCall gestoreNotificheCall(CalendarioService calendarioService,
                                                     ServizioNotifiche servizioNotifiche) {
        GestoreNotificheCall gestore = new GestoreNotificheCall();
        gestore.registra(new CalendarioObserver(calendarioService));
        gestore.registra(new NotificaControparteObserver(servizioNotifiche));
        return gestore;
    }

    @Bean
    public ProponiCallService proponiCallService(TeamRepository teamRepository,
                                                 CallPropostaRepository callPropostaRepository,
                                                 GestoreNotificheCall gestoreNotificheCall) {
        return new ProponiCallService(teamRepository, callPropostaRepository, gestoreNotificheCall);
    }

    @Bean
    public RispondiCallPropostaService rispondiCallPropostaService(CallPropostaRepository callPropostaRepository,
                                                                   GestoreNotificheCall gestoreNotificheCall) {
        return new RispondiCallPropostaService(callPropostaRepository, gestoreNotificheCall);
    }
}