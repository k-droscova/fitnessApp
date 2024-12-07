package cz.cvut.fit.tjv.fitnessApp.service;

import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Room;
import cz.cvut.fit.tjv.fitnessApp.repository.ClassTypeRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.FitnessClassRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class RoomServiceImpl extends CrudServiceImpl<Room, Long> implements RoomService {

    private final RoomRepository roomRepository;
    private final ClassTypeRepository classTypeRepository;
    private final FitnessClassRepository fitnessClassRepository;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository,
                           ClassTypeRepository classTypeRepository,
                           FitnessClassRepository fitnessClassRepository) {
        this.roomRepository = roomRepository;
        this.classTypeRepository = classTypeRepository;
        this.fitnessClassRepository = fitnessClassRepository;
    }

    @Override
    protected CrudRepository<Room, Long> getRepository() {
        return roomRepository;
    }

    @Override
    public Room create(Room room) {
        // Handle ClassType associations
        if (room.getClassTypes() != null) {
            List<ClassType> resolvedClassTypes = room.getClassTypes().stream()
                    .map(classType -> classTypeRepository.findById(classType.getId())
                            .orElseThrow(() -> new IllegalArgumentException("ClassType not found: " + classType.getId())))
                    .toList();
            room.setClassTypes(resolvedClassTypes);
            resolvedClassTypes.forEach(classType -> classType.getRooms().add(room));
        }

        // Handle FitnessClass associations
        if (room.getClasses() != null) {
            List<FitnessClass> resolvedFitnessClasses = room.getClasses().stream()
                    .map(fitnessClass -> fitnessClassRepository.findById(fitnessClass.getId())
                            .orElseThrow(() -> new IllegalArgumentException("FitnessClass not found: " + fitnessClass.getId())))
                    .toList();
            room.setClasses(resolvedFitnessClasses);
            resolvedFitnessClasses.forEach(fitnessClass -> fitnessClass.setRoom(room));
        }

        // Save and return the Room
        return super.create(room);
    }

    @Override
    public Room update(Long id, Room updatedRoom) {
        // Fetch the existing room from the database
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + id));

        // Update basic attributes
        existingRoom.setMaxCapacity(updatedRoom.getMaxCapacity());

        // Handle class types (Many-to-Many relationship)
        existingRoom.getClassTypes().forEach(classType -> classType.getRooms().remove(existingRoom));
        existingRoom.getClassTypes().clear();
        if (updatedRoom.getClassTypes() != null) {
            updatedRoom.getClassTypes().forEach(classType -> {
                ClassType resolvedClassType = classTypeRepository.findById(classType.getId())
                        .orElseThrow(() -> new IllegalArgumentException("ClassType not found with ID: " + classType.getId()));
                resolvedClassType.getRooms().add(existingRoom);
                existingRoom.getClassTypes().add(resolvedClassType);
            });
        }

        // Handle fitness classes (One-to-Many relationship)
        existingRoom.getClasses().forEach(fitnessClass -> fitnessClass.setRoom(null));
        existingRoom.getClasses().clear();
        if (updatedRoom.getClasses() != null) {
            updatedRoom.getClasses().forEach(fitnessClass -> {
                FitnessClass resolvedFitnessClass = fitnessClassRepository.findById(fitnessClass.getId())
                        .orElseThrow(() -> new IllegalArgumentException("FitnessClass not found with ID: " + fitnessClass.getId()));
                resolvedFitnessClass.setRoom(existingRoom);
                existingRoom.getClasses().add(resolvedFitnessClass);
            });
        }

        // Save and return the updated room
        return roomRepository.save(existingRoom);
    }

    @Override
    public List<Room> findAvailableRooms(Optional<Long> classTypeId, LocalDate date, LocalTime time) {
        return roomRepository.findAvailableRoomsByOptionalClassType(
                classTypeId.orElse(null), // If empty, pass null to fetch all rooms
                date,
                time
        );
    }

    @Override
    public List<Room> findByClassTypeName(String classTypeName) {
        List<ClassType> classTypes = classTypeRepository.findByNameContainingIgnoreCase(classTypeName);
        List<Long> classTypeIds = classTypes.stream()
                .map(ClassType::getId)
                .toList();
        return roomRepository.findAllByClassTypeIds(classTypeIds);
    }
}