package api;

import application.*;
import domain.repository.AccountRepository;
import domain.repository.MembroStaffRepository;
import domain.repository.UtenteRepository;
import infrastructure.repository.InMemoryAccountRepository;
import infrastructure.repository.InMemoryMembroStaffRepository;
import infrastructure.repository.InMemoryUtenteRepository;
import domain.repository.HackathonRepository;
import infrastructure.repository.InMemoryHackathonRepository;
import domain.repository.TeamRepository;
import domain.repository.InvitoRepository;
import infrastructure.repository.InMemoryTeamRepository;
import infrastructure.repository.InMemoryInvitoRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import domain.repository.SottomissioneRepository;
import infrastructure.repository.InMemorySottomissioneRepository;


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
}