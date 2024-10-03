package cz.cvut.fit.tjv.fitnessApp.respository;

import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import java.util.Optional;

public interface InstructorRepository extends CrudRepository<Instructor, Integer> {
    Optional<Instructor> findByName(String name);
}
