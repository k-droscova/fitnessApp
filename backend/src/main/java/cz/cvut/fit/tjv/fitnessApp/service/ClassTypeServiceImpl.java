package cz.cvut.fit.tjv.fitnessApp.service;

import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.domain.Room;
import cz.cvut.fit.tjv.fitnessApp.repository.ClassTypeRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.FitnessClassRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.InstructorRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ClassTypeServiceImpl extends CrudServiceImpl<ClassType, Long> implements ClassTypeService {

    private final ClassTypeRepository classTypeRepository;
    private final InstructorRepository instructorRepository;
    private final RoomRepository roomRepository;
    private final FitnessClassRepository fitnessClassRepository;

    @Autowired
    public ClassTypeServiceImpl(ClassTypeRepository classTypeRepository, InstructorRepository instructorRepository, RoomRepository roomRepository, FitnessClassRepository fitnessClassRepository) {
        this.classTypeRepository = classTypeRepository;
        this.instructorRepository = instructorRepository;
        this.roomRepository = roomRepository;
        this.fitnessClassRepository = fitnessClassRepository;
    }

    @Override
    protected ClassTypeRepository getRepository() {
        return classTypeRepository;
    }

    @Override
    public List<Instructor> findInstructorsById(Long classTypeId) {
        return classTypeRepository.findById(classTypeId)
                .map(ClassType::getInstructors)
                .orElse(Collections.emptyList());
    }

    @Override
    public List<FitnessClass> findFitnessClassesById(Long classTypeId) {
        return classTypeRepository.findById(classTypeId)
                .map(ClassType::getClasses)
                .orElse(Collections.emptyList());
    }

    @Override
    public List<Room> findRoomsById(Long classTypeId) {
        return classTypeRepository.findById(classTypeId)
                .map(ClassType::getRooms)
                .orElse(Collections.emptyList());
    }

    @Override
    public List<ClassType> readAllByName(String name) {
        return new ArrayList<>(classTypeRepository.findByNameContainingIgnoreCase(name));
    }

    @Override
    public ClassType update(Long id, ClassType updatedClassType) {
        // Call the parent update method to handle the basic update logic
        ClassType savedClassType = super.update(id, updatedClassType);

        // Update reverse associations (instructors and rooms)

        // Update instructors
        savedClassType.getInstructors().forEach(instructor -> instructor.getSpecializations().remove(savedClassType));
        savedClassType.getInstructors().clear();
        updatedClassType.getInstructors().forEach(instructor -> {
            instructor.getSpecializations().add(savedClassType);
            savedClassType.getInstructors().add(instructor);
        });

        // Update rooms
        savedClassType.getRooms().forEach(room -> room.getClassTypes().remove(savedClassType));
        savedClassType.getRooms().clear();
        updatedClassType.getRooms().forEach(room -> {
            room.getClassTypes().add(savedClassType);
            savedClassType.getRooms().add(room);
        });

        // Update fitness classes
        savedClassType.getClasses().forEach(fitnessClass -> fitnessClass.setClassType(null));
        savedClassType.getClasses().clear();
        updatedClassType.getClasses().forEach(fitnessClass -> {
            fitnessClass.setClassType(savedClassType);
            savedClassType.getClasses().add(fitnessClass);
        });

        // Save changes and return the updated entity
        return savedClassType;
    }

    @Override
    public ClassType create(ClassType classType) {
        // Resolve Instructor associations
        if (classType.getInstructors() != null) {
            List<Instructor> instructors = classType.getInstructors().stream()
                    .map(instructor -> instructorRepository.findById(instructor.getId())
                            .orElseThrow(() -> new IllegalArgumentException("Instructor not found: " + instructor.getId())))
                    .toList();
            classType.setInstructors(new ArrayList<>(instructors)); // Ensure modifiable
            instructors.forEach(instructor -> instructor.getSpecializations().add(classType));
        } else {
            classType.setInstructors(new ArrayList<>()); // Initialize empty list
        }

        // Resolve Room associations
        if (classType.getRooms() != null) {
            List<Room> rooms = classType.getRooms().stream()
                    .map(room -> roomRepository.findById(room.getId())
                            .orElseThrow(() -> new IllegalArgumentException("Room not found: " + room.getId())))
                    .toList();
            classType.setRooms(new ArrayList<>(rooms)); // Ensure modifiable
            rooms.forEach(room -> room.getClassTypes().add(classType));
        } else {
            classType.setRooms(new ArrayList<>()); // Initialize empty list
        }

        // Resolve FitnessClass associations
        if (classType.getClasses() != null) {
            List<FitnessClass> fitnessClasses = classType.getClasses().stream()
                    .map(fitnessClass -> fitnessClassRepository.findById(fitnessClass.getId())
                            .orElseThrow(() -> new IllegalArgumentException("FitnessClass not found: " + fitnessClass.getId())))
                    .toList();
            classType.setClasses(new ArrayList<>(fitnessClasses)); // Ensure modifiable
            fitnessClasses.forEach(fitnessClass -> fitnessClass.setClassType(classType));
        } else {
            classType.setClasses(new ArrayList<>()); // Initialize empty list
        }

        // Save and return the created ClassType
        return super.create(classType);
    }
}