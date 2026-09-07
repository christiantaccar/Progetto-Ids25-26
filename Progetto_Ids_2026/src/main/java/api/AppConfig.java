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
}