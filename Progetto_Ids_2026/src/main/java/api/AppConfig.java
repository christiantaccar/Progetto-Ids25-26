package api;

import application.EffettuaAccessoService;
import application.RegistrazioneService;
import domain.repository.AccountRepository;
import domain.repository.MembroStaffRepository;
import domain.repository.UtenteRepository;
import infrastructure.repository.InMemoryAccountRepository;
import infrastructure.repository.InMemoryMembroStaffRepository;
import infrastructure.repository.InMemoryUtenteRepository;
import application.CreateHackathonService;
import domain.repository.HackathonRepository;
import infrastructure.repository.InMemoryHackathonRepository;
import application.InvitaMembriService;
import application.CreaTeamService;
import domain.repository.TeamRepository;
import domain.repository.InvitoRepository;
import infrastructure.repository.InMemoryTeamRepository;
import infrastructure.repository.InMemoryInvitoRepository;
import application.VisualizzaInvitiService;
import application.UnisciTeamService;
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
}