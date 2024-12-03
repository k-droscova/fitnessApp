package cz.cvut.fit.tjv.fitnessApp.service;

import cz.cvut.fit.tjv.fitnessApp.domain.Trainee;

import java.util.List;

public interface TraineeService extends CrudService<Trainee, Long> {
    List<Trainee> findTraineesByFitnessClassId(Long fitnessClassId);
}
