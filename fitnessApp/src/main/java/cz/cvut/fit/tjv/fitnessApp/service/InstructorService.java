package cz.cvut.fit.tjv.fitnessApp.service;

import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;

import java.util.List;

public interface InstructorService extends CrudService<Instructor, Long> {
    List<Instructor> readAllByName(String name);
    List<Instructor> readAllBySurname(String surname);
    List<Instructor> readAllByNameOrSurname(String input);
}
