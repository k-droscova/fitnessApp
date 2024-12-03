package cz.cvut.fit.tjv.fitnessApp.service.mappers;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.TraineeDto;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Trainee;
import cz.cvut.fit.tjv.fitnessApp.repository.FitnessClassRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TraineeMapper implements EntityMapper<Trainee, TraineeDto> {

    private final FitnessClassRepository fitnessClassRepository;

    public TraineeMapper(FitnessClassRepository fitnessClassRepository) {
        this.fitnessClassRepository = fitnessClassRepository;
    }

    @Override
    public Trainee convertToEntity(TraineeDto traineeDto) {
        Trainee trainee = new Trainee();
        trainee.setId(traineeDto.getId());
        trainee.setEmail(traineeDto.getEmail());
        trainee.setName(traineeDto.getName());
        trainee.setSurname(traineeDto.getSurname());

        // Map FitnessClass IDs to entities
        if (traineeDto.getFitnessClassIds() != null) {
            List<FitnessClass> classes = traineeDto.getFitnessClassIds().stream()
                    .map(id -> fitnessClassRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("FitnessClass not found for ID: " + id)))
                    .collect(Collectors.toList());
            trainee.setClasses(classes);
        }

        return trainee;
    }

    @Override
    public TraineeDto convertToDto(Trainee trainee) {
        TraineeDto traineeDto = new TraineeDto();
        traineeDto.setId(trainee.getId());
        traineeDto.setEmail(trainee.getEmail());
        traineeDto.setName(trainee.getName());
        traineeDto.setSurname(trainee.getSurname());

        // Map FitnessClass entities to IDs
        if (trainee.getClasses() != null) {
            List<Long> fitnessClassIds = trainee.getClasses().stream()
                    .map(FitnessClass::getId)
                    .collect(Collectors.toList());
            traineeDto.setFitnessClassIds(fitnessClassIds);
        }

        return traineeDto;
    }

    @Override
    public List<TraineeDto> convertManyToDto(List<Trainee> entities) {
        return entities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}