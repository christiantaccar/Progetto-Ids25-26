package domain.repository;

import domain.models.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    void save(Account account);
    Optional<Account> findById(UUID id);
    Optional<Account> findByEmail(String email);
    boolean existsByEmail(String email);
}
