package cz.cvut.fit.tjv.fitnessApp.service;

import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface InstructorService extends CrudService<Instructor, Long> {
    List<Instructor> readAllByName(String name);
    List<Instructor> readAllBySurname(String surname);
    List<Instructor> readAllByNameOrSurname(String input);
    List<Instructor> findAvailableInstructors(Optional<Long> classTypeId, LocalDate date, LocalTime time);
}
