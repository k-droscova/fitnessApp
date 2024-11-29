package cz.cvut.fit.tjv.fitnessApp.service;

import cz.cvut.fit.tjv.fitnessApp.domain.Trainee;
import cz.cvut.fit.tjv.fitnessApp.repository.TraineeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraineeServiceImpl extends CrudServiceImpl<Trainee, Long> implements TraineeService {

    private final TraineeRepository traineeRepository;

    @Autowired
    public TraineeServiceImpl(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Override
    protected CrudRepository<Trainee, Long> getRepository() {
        return traineeRepository;
    }

    @Override
    public List<Trainee> findTraineesByFitnessClassId(Long fitnessClassId) {
        if (fitnessClassId == null) {
            throw new IllegalArgumentException("FitnessClass ID must not be null");
        }
        return traineeRepository.findByFitnessClassId(fitnessClassId);
    }
}