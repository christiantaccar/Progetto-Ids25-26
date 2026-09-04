package infrastructure.repository;

import domain.models.MembroStaff;
import domain.repository.MembroStaffRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class InMemoryMembroStaffRepository implements MembroStaffRepository {

    private final Map<UUID, MembroStaff> storage = new HashMap<>();

    @Override
    public void save(MembroStaff membroStaff) {
        Objects.requireNonNull(membroStaff);
        storage.put(membroStaff.getId(), membroStaff);
    }

    @Override
    public Optional<MembroStaff> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<MembroStaff> findAll() {
        return List.copyOf(new ArrayList<>(storage.values()));
    }
}
