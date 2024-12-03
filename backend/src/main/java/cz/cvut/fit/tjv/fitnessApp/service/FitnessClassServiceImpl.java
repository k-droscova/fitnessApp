package cz.cvut.fit.tjv.fitnessApp.service;

import cz.cvut.fit.tjv.fitnessApp.domain.*;
import cz.cvut.fit.tjv.fitnessApp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Service
public class FitnessClassServiceImpl extends CrudServiceImpl<FitnessClass, Long> implements FitnessClassService {

    private final FitnessClassRepository fitnessClassRepository;
    private final TraineeRepository traineeRepository;
    private final InstructorRepository instructorRepository;
    private final RoomRepository roomRepository;
    private final ClassTypeRepository classTypeRepository;

    @Autowired
    public FitnessClassServiceImpl(FitnessClassRepository fitnessClassRepository, TraineeRepository traineeRepository, InstructorRepository instructorRepository, RoomRepository roomRepository, ClassTypeRepository classTypeRepository) {
        this.fitnessClassRepository = fitnessClassRepository;
        this.traineeRepository = traineeRepository;
        this.instructorRepository = instructorRepository;
        this.roomRepository = roomRepository;
        this.classTypeRepository = classTypeRepository;
    }

    @Override
    protected CrudRepository<FitnessClass, Long> getRepository() {
        return fitnessClassRepository;
    }

    @Override
    public List<Trainee> findTraineesById(Long fitnessClassId) {
        return fitnessClassRepository.findById(fitnessClassId)
                .map(FitnessClass::getTrainees)
                .orElse(Collections.emptyList());
    }

    @Override
    public List<FitnessClass> readAllByDate(LocalDate date) {
        return fitnessClassRepository.findFitnessClassesByDate(date);
    }

    @Override
    public List<FitnessClass> readAllByDateAndTimeBetween(LocalDate date, LocalTime start, LocalTime end) {
        return fitnessClassRepository.findFitnessClassesByTimeBetweenAndDate(start, end, date);
    }

    @Override
    public List<FitnessClass> readAllByDateAndRoomId(LocalDate date, Long roomId) {
        return fitnessClassRepository.findFitnessClassesByDateAndRoom_Id(date, roomId);
    }

    @Override
    public void scheduleClass(FitnessClass fitnessClass) {
        validateFutureDateTime(fitnessClass.getDate(), fitnessClass.getTime(), "FitnessClass must be scheduled in the future.");
        boolean isRoomAvailable = isRoomAvailable(fitnessClass, null); // No ID to exclude for new classes
        boolean isInstructorAvailable = isInstructorAvailable(fitnessClass, null);
        validateClassCapacity(fitnessClass.getCapacity(), fitnessClass.getRoom());

        if (!isRoomAvailable || !isInstructorAvailable) {
            String errorMessage = formatConflictError(!isRoomAvailable, !isInstructorAvailable);
            throw new IllegalArgumentException(errorMessage); // Throwing an exception to indicate conflict
        }

        create(fitnessClass); // Use the generic `create` method
    }

    @Override
    public void validateAndUpdate(Long id, FitnessClass updatedClass) {
        FitnessClass existingClass = fitnessClassRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FitnessClass with ID " + id + " not found."));

        validateFutureDateTime(updatedClass.getDate(), updatedClass.getTime(), "Updated FitnessClass must be scheduled in the future.");
        boolean roomConflict = !isRoomAvailable(updatedClass, id);
        boolean instructorConflict = !isInstructorAvailable(updatedClass, id);
        validateClassCapacity(updatedClass.getCapacity(), updatedClass.getRoom());

        if (roomConflict || instructorConflict) {
            throw new IllegalArgumentException(formatConflictError(roomConflict, instructorConflict));
        }

        // Update associations
        // Update Room association
        if (existingClass.getRoom() != null) {
            existingClass.getRoom().getClasses().remove(existingClass);
        }
        if (updatedClass.getRoom() != null) {
            Room room = roomRepository.findById(updatedClass.getRoom().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Room with ID " + updatedClass.getRoom().getId() + " not found."));
            room.getClasses().add(existingClass);
            existingClass.setRoom(room);
        }

        // Update Instructor association
        if (existingClass.getInstructor() != null) {
            existingClass.getInstructor().getClasses().remove(existingClass);
        }
        if (updatedClass.getInstructor() != null) {
            Instructor instructor = instructorRepository.findById(updatedClass.getInstructor().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Instructor with ID " + updatedClass.getInstructor().getId() + " not found."));
            instructor.getClasses().add(existingClass);
            existingClass.setInstructor(instructor);
        }

        // Update ClassType association
        if (existingClass.getClassType() != null) {
            existingClass.getClassType().getClasses().remove(existingClass);
        }
        if (updatedClass.getClassType() != null) {
            ClassType classType = classTypeRepository.findById(updatedClass.getClassType().getId())
                    .orElseThrow(() -> new IllegalArgumentException("ClassType with ID " + updatedClass.getClassType().getId() + " not found."));
            classType.getClasses().add(existingClass);
            existingClass.setClassType(classType);
        }

        // Update the existing class with new details
        existingClass.setCapacity(updatedClass.getCapacity());
        existingClass.setDate(updatedClass.getDate());
        existingClass.setTime(updatedClass.getTime());

        fitnessClassRepository.save(existingClass);
    }

    @Override
    public void addTraineeToClass(Long fitnessClassId, Long traineeId) {
        // Fetch the fitness class
        FitnessClass fitnessClass = fitnessClassRepository.findById(fitnessClassId)
                .orElseThrow(() -> new IllegalArgumentException("FitnessClass not found"));

        // Fetch the trainee
        Trainee trainee = traineeRepository.findById(traineeId)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found"));

        validateFutureDateTime(fitnessClass.getDate(), fitnessClass.getTime(), "Cannot sign up for class in the past.");

        // Validate capacity
        if (fitnessClass.getTrainees().size() >= fitnessClass.getCapacity()) {
            throw new IllegalArgumentException("FitnessClass is already full.");
        }

        // Validate duplicate enrollment
        if (fitnessClass.getTrainees().contains(trainee)) {
            throw new IllegalArgumentException("Trainee is already enrolled.");
        }

        // Add the trainee and save
        fitnessClass.getTrainees().add(trainee);
        trainee.getClasses().add(fitnessClass);
        traineeRepository.save(trainee);
        fitnessClassRepository.save(fitnessClass);
    }

    // PRIVATE HELPERS

    private boolean isRoomAvailable(FitnessClass fitnessClass, Long excludeId) {
        return fitnessClassRepository
                .findFitnessClassesByDateAndRoom_Id(fitnessClass.getDate(), fitnessClass.getRoom().getId())
                .stream()
                .noneMatch(fc -> !fc.getId().equals(excludeId) && fc.getTime().equals(fitnessClass.getTime()));
    }

    private boolean isInstructorAvailable(FitnessClass fitnessClass, Long excludeId) {
        return fitnessClassRepository
                .findFitnessClassesByDate(fitnessClass.getDate())
                .stream()
                .noneMatch(fc -> !fc.getId().equals(excludeId)
                        && fc.getInstructor().getId().equals(fitnessClass.getInstructor().getId())
                        && fc.getTime().equals(fitnessClass.getTime()));
    }

    private String formatConflictError(boolean roomConflict, boolean instructorConflict) {
        StringBuilder error = new StringBuilder("Scheduling conflict detected: ");
        if (roomConflict) error.append("Room is already booked. ");
        if (instructorConflict) error.append("Instructor is unavailable. ");
        return error.toString();
    }

    private void validateFutureDateTime(LocalDate date, LocalTime time, String errorMessage) {
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private void validateClassCapacity(int capacity, Room room) {
        if (capacity <= 0 || capacity > room.getMaxCapacity()) {
            throw new IllegalArgumentException(
                    "Class capacity must be greater than 0 and less than or equal to the room's maximum capacity."
            );
        }
    }
}