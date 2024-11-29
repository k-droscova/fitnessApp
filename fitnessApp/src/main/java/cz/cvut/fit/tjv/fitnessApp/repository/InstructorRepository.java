package cz.cvut.fit.tjv.fitnessApp.repository;

import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstructorRepository extends CrudRepository<Instructor, Long> {
    List<Instructor> findInstructorByNameStartingWithIgnoreCase(String name);

    List<Instructor> findInstructorsBySurnameStartingWithIgnoreCase(String surname);

    @Query("SELECT i FROM Instructor i WHERE LOWER(i.name) LIKE LOWER(CONCAT(:input, '%')) OR LOWER(i.surname) LIKE LOWER(CONCAT(:input, '%'))")
    List<Instructor> findInstructorsByNameOrSurnameStartingWithIgnoreCase(@Param("input") String input);
}
