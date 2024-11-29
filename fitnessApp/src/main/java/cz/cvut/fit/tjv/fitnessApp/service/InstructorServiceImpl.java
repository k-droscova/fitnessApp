package cz.cvut.fit.tjv.fitnessApp.service;

import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
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

    @Autowired
    public InstructorServiceImpl(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    @Override
    protected CrudRepository<Instructor, Long> getRepository() {
        return instructorRepository;
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