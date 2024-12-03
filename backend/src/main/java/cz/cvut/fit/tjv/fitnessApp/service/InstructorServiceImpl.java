package cz.cvut.fit.tjv.fitnessApp.service;

import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.repository.ClassTypeRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.FitnessClassRepository;
import cz.cvut.fit.tjv.fitnessApp.repository.InstructorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class InstructorServiceImpl extends CrudServiceImpl<Instructor, Long> implements InstructorService {

    private final InstructorRepository instructorRepository;
    private final ClassTypeRepository classTypeRepository;
    private final FitnessClassRepository fitnessClassRepository;

    @Autowired
    public InstructorServiceImpl(InstructorRepository instructorRepository,
                                 ClassTypeRepository classTypeRepository,
                                 FitnessClassRepository fitnessClassRepository) {
        this.instructorRepository = instructorRepository;
        this.classTypeRepository = classTypeRepository;
        this.fitnessClassRepository = fitnessClassRepository;
    }

    @Override
    protected CrudRepository<Instructor, Long> getRepository() {
        return instructorRepository;
    }

    @Override
    public Instructor create(Instructor instructor) {
        // Resolve ClassType associations
        if (instructor.getSpecializations() != null) {
            List<ClassType> specializations = instructor.getSpecializations().stream()
                    .map(classType -> classTypeRepository.findById(classType.getId())
                            .orElseThrow(() -> new IllegalArgumentException("ClassType not found: " + classType.getId())))
                    .toList();
            instructor.setSpecializations(specializations);
            specializations.forEach(classType -> classType.getInstructors().add(instructor));
        }

        // Resolve FitnessClass associations
        if (instructor.getClasses() != null) {
            List<FitnessClass> fitnessClasses = instructor.getClasses().stream()
                    .map(fitnessClass -> fitnessClassRepository.findById(fitnessClass.getId())
                            .orElseThrow(() -> new IllegalArgumentException("FitnessClass not found: " + fitnessClass.getId())))
                    .toList();
            instructor.setClasses(fitnessClasses);
            fitnessClasses.forEach(fitnessClass -> fitnessClass.setInstructor(instructor));
        }

        // Create and return the instructor
        return super.create(instructor);
    }

    @Override
    public Instructor update(Long id, Instructor updatedInstructor) {
        // Fetch the existing instructor from the database
        Instructor existingInstructor = instructorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found with ID: " + id));

        // Update basic attributes
        existingInstructor.setName(updatedInstructor.getName());
        existingInstructor.setSurname(updatedInstructor.getSurname());
        existingInstructor.setBirthDate(updatedInstructor.getBirthDate());

        // Handle specializations (Many-to-Many relationship)
        existingInstructor.getSpecializations().forEach(classType -> classType.getInstructors().remove(existingInstructor));
        existingInstructor.getSpecializations().clear();
        updatedInstructor.getSpecializations().forEach(classType -> {
            ClassType resolvedClassType = classTypeRepository.findById(classType.getId())
                    .orElseThrow(() -> new IllegalArgumentException("ClassType not found with ID: " + classType.getId()));
            resolvedClassType.getInstructors().add(existingInstructor);
            existingInstructor.getSpecializations().add(resolvedClassType);
        });

        // Handle fitness classes (One-to-Many relationship)
        existingInstructor.getClasses().forEach(fitnessClass -> fitnessClass.setInstructor(null));
        existingInstructor.getClasses().clear();
        updatedInstructor.getClasses().forEach(fitnessClass -> {
            FitnessClass resolvedFitnessClass = fitnessClassRepository.findById(fitnessClass.getId())
                    .orElseThrow(() -> new IllegalArgumentException("FitnessClass not found with ID: " + fitnessClass.getId()));
            resolvedFitnessClass.setInstructor(existingInstructor);
            existingInstructor.getClasses().add(resolvedFitnessClass);
        });

        // Save and return the updated instructor
        return instructorRepository.save(existingInstructor);
    }

    @Override
    public List<Instructor> readAllByName(String name) {
        return instructorRepository.findInstructorByNameStartingWithIgnoreCase(name);
    }

    @Override
    public List<Instructor> readAllBySurname(String surname) {
        return instructorRepository.findInstructorsBySurnameStartingWithIgnoreCase(surname);
    }

    @Override
    public List<Instructor> readAllByNameOrSurname(String input) {
        return instructorRepository.findInstructorsByNameOrSurnameStartingWithIgnoreCase(input);
    }

    @Override
    public List<Instructor> findAvailableInstructors(Optional<Long> classTypeId, LocalDate date, LocalTime time) {
        return instructorRepository.findAvailableInstructorsByOptionalClassType(
                classTypeId.orElse(null), // If empty, pass null to fetch all instructors
                date,
                time
        );
    }
}