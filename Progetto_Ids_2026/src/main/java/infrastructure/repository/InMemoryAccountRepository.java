package infrastructure.repository;

import domain.models.Account;
import domain.models.PersonaRegistrata;
import domain.repository.AccountRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class InMemoryAccountRepository implements AccountRepository {

    private final Map<UUID, Account> storage = new HashMap<>();

    @Override
    public void save(Account account) {
        Objects.requireNonNull(account);
        storage.put(account.getId(), account);
    }

    @Override
    public Optional<Account> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<Account> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        String cercata = PersonaRegistrata.normalizzaEmail(email);
        return storage.values().stream()
                .filter(a -> a.getEmail().equals(cercata))
                .findFirst();
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }
}
