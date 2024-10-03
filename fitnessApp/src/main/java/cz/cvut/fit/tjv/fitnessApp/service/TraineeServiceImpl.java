package cz.cvut.fit.tjv.fitnessApp.service;

import cz.cvut.fit.tjv.fitnessApp.domain.Trainee;
import cz.cvut.fit.tjv.fitnessApp.respository.CrudRepository;
import cz.cvut.fit.tjv.fitnessApp.respository.TraineeRepository;

public class TraineeServiceImpl extends CrudServiceImpl<Trainee, Integer> implements TraineeService {
    private TraineeRepository traineeRepository;
    @Override
    protected CrudRepository<Trainee, Integer> getRepository() {
        return traineeRepository;
    }
}