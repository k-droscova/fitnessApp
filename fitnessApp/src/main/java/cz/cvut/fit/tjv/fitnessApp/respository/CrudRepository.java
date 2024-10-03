package cz.cvut.fit.tjv.fitnessApp.respository;

import java.util.Optional;
import cz.cvut.fit.tjv.fitnessApp.domain.Identifiable;

public interface CrudRepository<T extends Identifiable<ID>, ID> {
    T save(T entity);
    boolean existsById(ID id);
    Optional<T> findById(ID id);
    Iterable<T> findAll();
    void deleteById(ID id);
}