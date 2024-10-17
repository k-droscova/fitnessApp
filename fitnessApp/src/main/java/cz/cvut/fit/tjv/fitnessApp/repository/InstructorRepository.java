package cz.cvut.fit.tjv.fitnessApp.repository;

import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

@Repository
public interface InstructorRepository extends CrudRepository<Instructor, Integer> {
    Optional<Instructor> findByName(String name);
}
