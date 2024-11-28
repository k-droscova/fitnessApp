package cz.cvut.fit.tjv.fitnessApp.service.mappers;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.FitnessClassDto;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.domain.Room;
import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.domain.Trainee;
import cz.cvut.fit.tjv.fitnessApp.repository.InstructorRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.RoomRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.ClassTypeRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.TraineeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FitnessClassMapper implements EntityMapper<FitnessClass, FitnessClassDto> {

    private final InstructorRepository instructorRepository;
    private final RoomRepository roomRepository;
    private final ClassTypeRepository classTypeRepository;
    private final TraineeRepository traineeRepository;
    private final ModelMapper modelMapper;

    public FitnessClassMapper(InstructorRepository instructorRepository,
                              RoomRepository roomRepository,
                              ClassTypeRepository classTypeRepository,
                              TraineeRepository traineeRepository,
                              ModelMapper modelMapper) {
        this.instructorRepository = instructorRepository;
        this.roomRepository = roomRepository;
        this.classTypeRepository = classTypeRepository;
        this.traineeRepository = traineeRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public FitnessClass convertToEntity(FitnessClassDto fitnessClassDto) {
        FitnessClass fitnessClass = modelMapper.map(fitnessClassDto, FitnessClass.class);

        // Map instructor ID
        if (fitnessClassDto.getInstructorId() != null) {
            Instructor instructor = instructorRepository.findById(fitnessClassDto.getInstructorId())
                    .orElseThrow(() -> new IllegalArgumentException("Instructor not found for ID: " + fitnessClassDto.getInstructorId()));
            fitnessClass.setInstructor(instructor);
        }

        // Map room ID
        if (fitnessClassDto.getRoomId() != null) {
            Room room = roomRepository.findById(fitnessClassDto.getRoomId())
                    .orElseThrow(() -> new IllegalArgumentException("Room not found for ID: " + fitnessClassDto.getRoomId()));
            fitnessClass.setRoom(room);
        }

        // Map class type ID
        if (fitnessClassDto.getClassTypeId() != null) {
            ClassType classType = classTypeRepository.findById(fitnessClassDto.getClassTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("ClassType not found for ID: " + fitnessClassDto.getClassTypeId()));
            fitnessClass.setClassType(classType);
        }

        // Map trainee IDs
        if (fitnessClassDto.getTraineeIds() != null) {
            Set<Trainee> trainees = fitnessClassDto.getTraineeIds().stream()
                    .map(id -> traineeRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("Trainee not found for ID: " + id)))
                    .collect(Collectors.toSet());
            fitnessClass.setTrainees(trainees);
        }

        return fitnessClass;
    }

    @Override
    public FitnessClassDto convertToDto(FitnessClass fitnessClass) {
        return modelMapper.map(fitnessClass, FitnessClassDto.class);
    }

    @Override
    public List<FitnessClassDto> convertManyToDto(List<FitnessClass> entities) {
        return entities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}