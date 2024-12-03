package cz.cvut.fit.tjv.fitnessApp.service.mappers;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.FitnessClassDto;
import cz.cvut.fit.tjv.fitnessApp.domain.*;
import cz.cvut.fit.tjv.fitnessApp.repository.ClassTypeRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.InstructorRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.RoomRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.TraineeRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FitnessClassMapper implements EntityMapper<FitnessClass, FitnessClassDto> {

    private final InstructorRepository instructorRepository;
    private final RoomRepository roomRepository;
    private final ClassTypeRepository classTypeRepository;
    private final TraineeRepository traineeRepository;

    public FitnessClassMapper(InstructorRepository instructorRepository,
                              RoomRepository roomRepository,
                              ClassTypeRepository classTypeRepository,
                              TraineeRepository traineeRepository) {
        this.instructorRepository = instructorRepository;
        this.roomRepository = roomRepository;
        this.classTypeRepository = classTypeRepository;
        this.traineeRepository = traineeRepository;
    }

    @Override
    public FitnessClass convertToEntity(FitnessClassDto fitnessClassDto) {
        FitnessClass fitnessClass = new FitnessClass();
        fitnessClass.setId(fitnessClassDto.getId());
        fitnessClass.setCapacity(fitnessClassDto.getCapacity());
        fitnessClass.setDate(fitnessClassDto.getDate());
        fitnessClass.setTime(fitnessClassDto.getTime());

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
            List<Trainee> trainees = fitnessClassDto.getTraineeIds().stream()
                    .map(id -> traineeRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("Trainee not found for ID: " + id)))
                    .collect(Collectors.toList());
            fitnessClass.setTrainees(trainees);
        }

        return fitnessClass;
    }

    @Override
    public FitnessClassDto convertToDto(FitnessClass fitnessClass) {
        FitnessClassDto fitnessClassDto = new FitnessClassDto();
        fitnessClassDto.setId(fitnessClass.getId());
        fitnessClassDto.setCapacity(fitnessClass.getCapacity());
        fitnessClassDto.setDate(fitnessClass.getDate());
        fitnessClassDto.setTime(fitnessClass.getTime());

        // Map instructor to ID
        if (fitnessClass.getInstructor() != null) {
            fitnessClassDto.setInstructorId(fitnessClass.getInstructor().getId());
        }

        // Map room to ID
        if (fitnessClass.getRoom() != null) {
            fitnessClassDto.setRoomId(fitnessClass.getRoom().getId());
        }

        // Map class type to ID
        if (fitnessClass.getClassType() != null) {
            fitnessClassDto.setClassTypeId(fitnessClass.getClassType().getId());
        }

        // Map trainees to IDs
        if (fitnessClass.getTrainees() != null) {
            List<Long> traineeIds = fitnessClass.getTrainees().stream()
                    .map(Trainee::getId)
                    .collect(Collectors.toList());
            fitnessClassDto.setTraineeIds(traineeIds);
        }

        return fitnessClassDto;
    }

    @Override
    public List<FitnessClassDto> convertManyToDto(List<FitnessClass> entities) {
        return entities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}