package cz.cvut.fit.tjv.fitnessApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.repository.InstructorRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

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
}