package cz.cvut.fit.tjv.fitnessApp.service;

import cz.cvut.fit.tjv.fitnessApp.domain.Identifiable;

import java.util.Optional;

public interface CrudService<T extends Identifiable<ID>, ID> {
    T create(T entity);
    Optional<T> readById(ID id);
    Iterable<T> readAll();
    void update(ID id, T entity);
    void deleteById(ID id);
}