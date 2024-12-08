package cz.cvut.fit.tjv.fitnessApp.service;

import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Trainee;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface FitnessClassService extends CrudService<FitnessClass, Long> {
    List<Trainee> findTraineesById(Long fitnessClassId);
    List<FitnessClass> readAllByDate(LocalDate date);
    List<FitnessClass> readAllByDateAndTimeBetween(LocalDate date, LocalTime start, LocalTime end);
    List<FitnessClass> readAllByDateAndRoomId(LocalDate date, Long roomId);
    void scheduleClass(FitnessClass fitnessClass);
    void validateAndUpdate(Long id, FitnessClass updatedClass);
    void addTraineeToClass(Long fitnessClassId, Long traineeId);
    void deleteTraineeFromClass(Long fitnessClassId, Long traineeId);
}
