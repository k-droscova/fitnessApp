package cz.cvut.fit.tjv.fitnessApp.service.mappers;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.RoomDto;
import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Room;
import cz.cvut.fit.tjv.fitnessApp.repository.ClassTypeRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.FitnessClassRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RoomMapper implements EntityMapper<Room, RoomDto> {

    private final FitnessClassRepository fitnessClassRepository;
    private final ClassTypeRepository classTypeRepository;
    private final ModelMapper modelMapper;

    public RoomMapper(FitnessClassRepository fitnessClassRepository,
                      ClassTypeRepository classTypeRepository,
                      ModelMapper modelMapper) {
        this.fitnessClassRepository = fitnessClassRepository;
        this.classTypeRepository = classTypeRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Room convertToEntity(RoomDto roomDto) {
        Room room = modelMapper.map(roomDto, Room.class);

        // Map FitnessClass IDs
        if (roomDto.getFitnessClassIds() != null) {
            Set<FitnessClass> classes = roomDto.getFitnessClassIds().stream()
                    .map(id -> fitnessClassRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("FitnessClass not found for ID: " + id)))
                    .collect(Collectors.toSet());
            room.setClasses(classes);
        }

        // Map ClassType IDs
        if (roomDto.getClassTypeIds() != null) {
            Set<ClassType> classTypes = roomDto.getClassTypeIds().stream()
                    .map(id -> classTypeRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("ClassType not found for ID: " + id)))
                    .collect(Collectors.toSet());
            room.setClassTypes(classTypes);
        }

        return room;
    }

    @Override
    public RoomDto convertToDto(Room room) {
        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    public List<RoomDto> convertManyToDto(List<Room> entities) {
        return entities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}