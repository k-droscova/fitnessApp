package cz.cvut.fit.tjv.fitnessApp.repository;

import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;
import java.util.Set;

@Repository
public interface ClassTypeRepository extends CrudRepository<ClassType, Integer> {
    Set<ClassType> findByNameContainingIgnoreCase(String name);
}
