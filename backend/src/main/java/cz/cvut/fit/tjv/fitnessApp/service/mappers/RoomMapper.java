package cz.cvut.fit.tjv.fitnessApp.service.mappers;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.room.RoomDto;
import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Room;
import cz.cvut.fit.tjv.fitnessApp.repository.ClassTypeRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.FitnessClassRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoomMapper implements EntityMapper<Room, RoomDto> {

    private final FitnessClassRepository fitnessClassRepository;
    private final ClassTypeRepository classTypeRepository;

    public RoomMapper(FitnessClassRepository fitnessClassRepository,
                      ClassTypeRepository classTypeRepository) {
        this.fitnessClassRepository = fitnessClassRepository;
        this.classTypeRepository = classTypeRepository;
    }

    @Override
    public Room convertToEntity(RoomDto roomDto) {
        Room room = new Room();
        room.setId(roomDto.getId());
        room.setMaxCapacity(roomDto.getMaxCapacity());

        // Map FitnessClass IDs to entities
        if (roomDto.getFitnessClassIds() != null) {
            List<FitnessClass> classes = roomDto.getFitnessClassIds().stream()
                    .map(id -> fitnessClassRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("FitnessClass not found for ID: " + id)))
                    .collect(Collectors.toList());
            room.setClasses(classes);
        }

        // Map ClassType IDs to entities
        if (roomDto.getClassTypeIds() != null) {
            List<ClassType> classTypes = roomDto.getClassTypeIds().stream()
                    .map(id -> classTypeRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("ClassType not found for ID: " + id)))
                    .collect(Collectors.toList());
            room.setClassTypes(classTypes);
        }

        return room;
    }

    @Override
    public RoomDto convertToDto(Room room) {
        RoomDto roomDto = new RoomDto();
        roomDto.setId(room.getId());
        roomDto.setMaxCapacity(room.getMaxCapacity());

        // Map FitnessClass entities to IDs
        if (room.getClasses() != null) {
            List<Long> fitnessClassIds = room.getClasses().stream()
                    .map(FitnessClass::getId)
                    .collect(Collectors.toList());
            roomDto.setFitnessClassIds(fitnessClassIds);
        }

        // Map ClassType entities to IDs
        if (room.getClassTypes() != null) {
            List<Long> classTypeIds = room.getClassTypes().stream()
                    .map(ClassType::getId)
                    .collect(Collectors.toList());
            roomDto.setClassTypeIds(classTypeIds);
        }

        return roomDto;
    }

    @Override
    public List<RoomDto> convertManyToDto(List<Room> entities) {
        return entities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}