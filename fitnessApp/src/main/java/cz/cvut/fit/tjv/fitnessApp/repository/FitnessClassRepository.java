package cz.cvut.fit.tjv.fitnessApp.repository;

import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface FitnessClassRepository extends CrudRepository<FitnessClass, Long> {
    List<FitnessClass> findFitnessClassesByDate(LocalDate date);
    List<FitnessClass> findFitnessClassesByDateAndRoom_Id(LocalDate date, Long roomId);
    List<FitnessClass> findFitnessClassesByTimeBetweenAndDate(LocalTime timeAfter, LocalTime timeBefore, LocalDate date);
}
