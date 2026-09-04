package domain.repository;

import domain.models.CallProposta;
import domain.models.MembroStaff;
import domain.models.Team;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CallPropostaRepository {
    void save(CallProposta proposta);
    Optional<CallProposta> findById(UUID id);

    /** Proposta ancora pendente fra quel mentore e quel team, se esiste. */
    Optional<CallProposta> findPendentePer(MembroStaff mentore, Team team);

    List<CallProposta> findByTeam(Team team);
}
