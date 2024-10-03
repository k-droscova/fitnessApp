package cz.cvut.fit.tjv.fitnessApp.service;

import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.respository.CrudRepository;
import cz.cvut.fit.tjv.fitnessApp.respository.InstructorRepository;

public class InstructorServiceImpl extends CrudServiceImpl<Instructor, Integer> implements InstructorService {
    private InstructorRepository instructorRepository;
    @Override
    protected CrudRepository<Instructor, Integer> getRepository() {
        return instructorRepository;
    }
}