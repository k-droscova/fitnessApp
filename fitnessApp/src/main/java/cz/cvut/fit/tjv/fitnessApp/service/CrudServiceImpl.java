package cz.cvut.fit.tjv.fitnessApp.service;

import cz.cvut.fit.tjv.fitnessApp.domain.Identifiable;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public abstract class CrudServiceImpl<T extends Identifiable<ID>, ID> implements CrudService<T, ID> {
    protected abstract CrudRepository<T, ID> getRepository();
    @Override
    public T create(T entity) {
        if (getRepository().existsById(entity.getId())) {
            throw new IllegalArgumentException("Entity with ID " + entity.getId() + " already exists.");
        }
        return getRepository().save(entity);
    }

    @Override
    public Optional<T> readById(ID id) {
        return getRepository().findById(id);
    }

    @Override
    public Iterable<T> readAll() {
        return getRepository().findAll();
    }

    @Override
    public void update(ID id, T entity) {
        if (!getRepository().existsById(id)) {
            throw new IllegalArgumentException("Entity with ID " + id + " does not exist.");
        }
        getRepository().save(entity);
    }

    @Override
    public void deleteById(ID id) {
        if (!getRepository().existsById(id)) {
            throw new IllegalArgumentException("Entity with ID " + id + " does not exist.");
        }
        getRepository().deleteById(id);
    }
}