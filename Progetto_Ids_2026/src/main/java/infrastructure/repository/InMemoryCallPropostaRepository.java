package infrastructure.repository;

import domain.models.CallProposta;
import domain.models.MembroStaff;
import domain.models.Team;
import domain.repository.CallPropostaRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class InMemoryCallPropostaRepository implements CallPropostaRepository {

    private final Map<UUID, CallProposta> storage = new HashMap<>();

    @Override
    public void save(CallProposta proposta) {
        Objects.requireNonNull(proposta);
        storage.put(proposta.getId(), proposta);
    }

    @Override
    public Optional<CallProposta> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<CallProposta> findPendentePer(MembroStaff mentore, Team team) {
        return storage.values().stream()
                .filter(CallProposta::isPendente)
                .filter(p -> p.getMentore().equals(mentore))
                .filter(p -> p.getTeam().equals(team))
                .findFirst();
    }

    @Override
    public List<CallProposta> findByTeam(Team team) {
        List<CallProposta> risultato = new ArrayList<>();
        for (CallProposta p : storage.values()) {
            if (p.getTeam().equals(team)) {
                risultato.add(p);
            }
        }
        return List.copyOf(risultato);
    }
}
