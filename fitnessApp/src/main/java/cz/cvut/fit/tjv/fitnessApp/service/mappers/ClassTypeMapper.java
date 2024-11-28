package cz.cvut.fit.tjv.fitnessApp.service.mappers;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.ClassTypeDto;
import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.domain.Room;
import cz.cvut.fit.tjv.fitnessApp.repository.FitnessClassRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.InstructorRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.RoomRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ClassTypeMapper implements EntityMapper<ClassType, ClassTypeDto> {

    private final InstructorRepository instructorRepository;
    private final RoomRepository roomRepository;
    private final FitnessClassRepository fitnessClassRepository;
    private final ModelMapper modelMapper;

    public ClassTypeMapper(InstructorRepository instructorRepository,
                           RoomRepository roomRepository,
                           FitnessClassRepository fitnessClassRepository,
                           ModelMapper modelMapper) {
        this.instructorRepository = instructorRepository;
        this.roomRepository = roomRepository;
        this.fitnessClassRepository = fitnessClassRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ClassType convertToEntity(ClassTypeDto classTypeDto) {
        ClassType classType = modelMapper.map(classTypeDto, ClassType.class);

        // Map instructor IDs
        if (classTypeDto.getInstructorIds() != null) {
            Set<Instructor> instructors = classTypeDto.getInstructorIds().stream()
                    .map(id -> instructorRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("Instructor not found for ID: " + id)))
                    .collect(Collectors.toSet());
            classType.setInstructors(instructors);
        }

        // Map rooms IDs
        if (classTypeDto.getRoomIds() != null) {
            Set<Room> rooms = classTypeDto.getRoomIds().stream()
                    .map(id -> roomRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("Room not found for ID: " + id)))
                    .collect(Collectors.toSet());
            classType.setRooms(rooms);
        }

        // Map fitness classes IDs
        if (classTypeDto.getFitnessClassIds() != null) {
            Set<FitnessClass> fitnessClasses = classTypeDto.getFitnessClassIds().stream()
                    .map(id -> fitnessClassRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("Fitness class not found for ID: " + id)))
                    .collect(Collectors.toSet());
            classType.setClasses(fitnessClasses);
        }

        return classType;
    }

    @Override
    public ClassTypeDto convertToDto(ClassType classType) {
        return modelMapper.map(classType, ClassTypeDto.class);
    }

    @Override
    public List<ClassTypeDto> convertManyToDto(List<ClassType> entities) {
        return entities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
