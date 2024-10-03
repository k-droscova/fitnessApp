package cz.cvut.fit.tjv.fitnessApp.respository.inMemory;

import cz.cvut.fit.tjv.fitnessApp.domain.Identifiable;
import cz.cvut.fit.tjv.fitnessApp.respository.CrudRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class InMemoryRepository<T extends Identifiable<ID>, ID> implements CrudRepository<T, ID> {
    private final Map<ID, T> data = new HashMap<>();

    @Override
    public T save(T entity) {
        data.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public boolean existsById(ID id) {
        return data.containsKey(id);
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public Iterable<T> findAll() {
        return data.values();
    }

    @Override
    public void deleteById(ID id) {
        data.remove(id);
    }
}