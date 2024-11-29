package cz.cvut.fit.tjv.fitnessApp.repository;

import cz.cvut.fit.tjv.fitnessApp.domain.Trainee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TraineeRepository extends CrudRepository<Trainee, Long> {
    @Query("SELECT t FROM Trainee t JOIN t.classes fc WHERE fc.id = :classId")
    List<Trainee> findByFitnessClassId(@Param("classId") Long classId);
}
