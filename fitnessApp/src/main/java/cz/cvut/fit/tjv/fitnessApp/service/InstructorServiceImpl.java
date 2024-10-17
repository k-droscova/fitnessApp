package cz.cvut.fit.tjv.fitnessApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.repository.InstructorRepository;
import org.springframework.data.repository.CrudRepository;

@Service
public class InstructorServiceImpl extends CrudServiceImpl<Instructor, Integer> implements InstructorService {

    private final InstructorRepository instructorRepository;

    @Autowired
    public InstructorServiceImpl(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    @Override
    protected CrudRepository<Instructor, Integer> getRepository() {
        return instructorRepository;
    }
}