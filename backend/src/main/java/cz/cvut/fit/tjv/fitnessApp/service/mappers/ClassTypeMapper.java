package cz.cvut.fit.tjv.fitnessApp.service.mappers;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.classType.ClassTypeDto;
import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.domain.Room;
import cz.cvut.fit.tjv.fitnessApp.repository.FitnessClassRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.InstructorRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.RoomRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClassTypeMapper implements EntityMapper<ClassType, ClassTypeDto> {

    private final InstructorRepository instructorRepository;
    private final RoomRepository roomRepository;
    private final FitnessClassRepository fitnessClassRepository;

    public ClassTypeMapper(InstructorRepository instructorRepository,
                           RoomRepository roomRepository,
                           FitnessClassRepository fitnessClassRepository) {
        this.instructorRepository = instructorRepository;
        this.roomRepository = roomRepository;
        this.fitnessClassRepository = fitnessClassRepository;
    }

    @Override
    public ClassType convertToEntity(ClassTypeDto classTypeDto) {
        ClassType classType = new ClassType();
        classType.setId(classTypeDto.getId());
        classType.setName(classTypeDto.getName());

        // Map instructor IDs
        if (classTypeDto.getInstructorIds() != null) {
            List<Instructor> instructors = classTypeDto.getInstructorIds().stream()
                    .map(id -> instructorRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("Instructor not found for ID: " + id)))
                    .collect(Collectors.toList());
            classType.setInstructors(instructors);
        }

        // Map room IDs
        if (classTypeDto.getRoomIds() != null) {
            List<Room> rooms = classTypeDto.getRoomIds().stream()
                    .map(id -> roomRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("Room not found for ID: " + id)))
                    .collect(Collectors.toList());
            classType.setRooms(rooms);
        }

        // Map fitness class IDs
        if (classTypeDto.getFitnessClassIds() != null) {
            List<FitnessClass> fitnessClasses = classTypeDto.getFitnessClassIds().stream()
                    .map(id -> fitnessClassRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("Fitness class not found for ID: " + id)))
                    .collect(Collectors.toList());
            classType.setClasses(fitnessClasses);
        }

        return classType;
    }

    @Override
    public ClassTypeDto convertToDto(ClassType classType) {
        ClassTypeDto classTypeDto = new ClassTypeDto();
        classTypeDto.setId(classType.getId());
        classTypeDto.setName(classType.getName());

        // Map instructor entities to IDs
        if (classType.getInstructors() != null) {
            List<Long> instructorIds = classType.getInstructors().stream()
                    .map(Instructor::getId)
                    .collect(Collectors.toList());
            classTypeDto.setInstructorIds(instructorIds);
        }

        // Map room entities to IDs
        if (classType.getRooms() != null) {
            List<Long> roomIds = classType.getRooms().stream()
                    .map(Room::getId)
                    .collect(Collectors.toList());
            classTypeDto.setRoomIds(roomIds);
        }

        // Map fitness class entities to IDs
        if (classType.getClasses() != null) {
            List<Long> fitnessClassIds = classType.getClasses().stream()
                    .map(FitnessClass::getId)
                    .collect(Collectors.toList());
            classTypeDto.setFitnessClassIds(fitnessClassIds);
        }

        return classTypeDto;
    }

    @Override
    public List<ClassTypeDto> convertManyToDto(List<ClassType> entities) {
        return entities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
