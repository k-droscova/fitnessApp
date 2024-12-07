package cz.cvut.fit.tjv.fitnessApp.service;

import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Trainee;
import cz.cvut.fit.tjv.fitnessApp.repository.FitnessClassRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.TraineeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraineeServiceImpl extends CrudServiceImpl<Trainee, Long> implements TraineeService {

    private final TraineeRepository traineeRepository;
    private final FitnessClassRepository fitnessClassRepository;

    @Autowired
    public TraineeServiceImpl(TraineeRepository traineeRepository,
                              FitnessClassRepository fitnessClassRepository) {
        this.traineeRepository = traineeRepository;
        this.fitnessClassRepository = fitnessClassRepository;
    }

    @Override
    protected CrudRepository<Trainee, Long> getRepository() {
        return traineeRepository;
    }

    @Override
    public Trainee create(Trainee trainee) {
        // Handle FitnessClass associations
        if (trainee.getClasses() != null) {
            List<FitnessClass> fitnessClasses = trainee.getClasses().stream()
                    .map(fitnessClass -> fitnessClassRepository.findById(fitnessClass.getId())
                            .orElseThrow(() -> new IllegalArgumentException("FitnessClass not found with ID: " + fitnessClass.getId())))
                    .toList();

            trainee.setClasses(fitnessClasses);
            fitnessClasses.forEach(fitnessClass -> fitnessClass.getTrainees().add(trainee));
        }

        // Save and return the Trainee
        return super.create(trainee);
    }

    @Override
    public Trainee update(Long id, Trainee updatedTrainee) {
        // Fetch the existing trainee from the database
        Trainee existingTrainee = traineeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found with ID: " + id));

        // Update basic attributes
        existingTrainee.setEmail(updatedTrainee.getEmail());
        existingTrainee.setName(updatedTrainee.getName());
        existingTrainee.setSurname(updatedTrainee.getSurname());

        // Handle fitness class associations (Many-to-Many relationship)
        // Remove existing associations
        existingTrainee.getClasses().forEach(fitnessClass -> fitnessClass.getTrainees().remove(existingTrainee));
        existingTrainee.getClasses().clear();

        // Add new associations
        if (updatedTrainee.getClasses() != null) {
            updatedTrainee.getClasses().forEach(fitnessClass -> {
                FitnessClass resolvedFitnessClass = fitnessClassRepository.findById(fitnessClass.getId())
                        .orElseThrow(() -> new IllegalArgumentException("FitnessClass not found with ID: " + fitnessClass.getId()));
                resolvedFitnessClass.getTrainees().add(existingTrainee);
                existingTrainee.getClasses().add(resolvedFitnessClass);
            });
        }

        // Save and return the updated trainee
        return traineeRepository.save(existingTrainee);
    }

    @Override
    public List<Trainee> findTraineesByFitnessClassId(Long fitnessClassId) {
        if (fitnessClassId == null) {
            throw new IllegalArgumentException("FitnessClass ID must not be null");
        }
        return traineeRepository.findByFitnessClassId(fitnessClassId);
    }

    @Override
    public List<Trainee> findTraineesByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Trainee name must not be null");
        }
        return traineeRepository.findByNameOrSurnameStartingWithIgnoreCase(name);
    }
}