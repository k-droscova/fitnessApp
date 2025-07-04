package cz.cvut.fit.tjv.fitnessApp.service;

import cz.cvut.fit.tjv.fitnessApp.domain.Identifiable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class CrudServiceImpl<T extends Identifiable<ID>, ID> implements CrudService<T, ID> {
    protected abstract CrudRepository<T, ID> getRepository();
    @Override
    public T create(T entity) {
        if (entity.getId() != null) {
            throw new IllegalArgumentException("Id should not be set during creation");
        }
        return getRepository().save(entity);
    }

    @Override
    public Optional<T> readById(ID id) {
        return getRepository().findById(id);
    }

    @Override
    public List<T> readAll() {
        Iterable<T> iterable = getRepository().findAll();
        return StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public T update(ID id, T entity) {
        if (!getRepository().existsById(id)) {
            throw new IllegalArgumentException("Entity with ID " + id + " does not exist.");
        }
        entity.setId(id);
        return getRepository().save(entity);
    }

    @Override
    public void deleteById(ID id) {
        if (!getRepository().existsById(id)) {
            throw new IllegalArgumentException("Entity with ID " + id + " does not exist.");
        }
        getRepository().deleteById(id);
    }
}