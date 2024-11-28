package cz.cvut.fit.tjv.fitnessApp.service.mappers;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.TraineeDto;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Trainee;
import cz.cvut.fit.tjv.fitnessApp.repository.FitnessClassRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TraineeMapper implements EntityMapper<Trainee, TraineeDto> {

    private final FitnessClassRepository fitnessClassRepository;
    private final ModelMapper modelMapper;

    public TraineeMapper(FitnessClassRepository fitnessClassRepository, ModelMapper modelMapper) {
        this.fitnessClassRepository = fitnessClassRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Trainee convertToEntity(TraineeDto traineeDto) {
        Trainee trainee = modelMapper.map(traineeDto, Trainee.class);

        // Map FitnessClass IDs
        if (traineeDto.getFitnessClassIds() != null) {
            Set<FitnessClass> classes = traineeDto.getFitnessClassIds().stream()
                    .map(id -> fitnessClassRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("FitnessClass not found for ID: " + id)))
                    .collect(Collectors.toSet());
            trainee.setClasses(classes);
        }

        return trainee;
    }

    @Override
    public TraineeDto convertToDto(Trainee trainee) {
        return modelMapper.map(trainee, TraineeDto.class);
    }

    @Override
    public List<TraineeDto> convertManyToDto(List<Trainee> entities) {
        return entities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}