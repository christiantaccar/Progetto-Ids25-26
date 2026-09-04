package domain.repository;

import domain.models.MembroStaff;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembroStaffRepository {
    void save(MembroStaff membroStaff);
    Optional<MembroStaff> findById(UUID id);
    List<MembroStaff> findAll();
}
