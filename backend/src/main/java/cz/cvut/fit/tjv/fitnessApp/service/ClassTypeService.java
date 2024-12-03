package cz.cvut.fit.tjv.fitnessApp.service;

import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.domain.Room;

import java.util.List;

public interface ClassTypeService extends CrudService<ClassType, Long> {
    List<Instructor> findInstructorsById(Long classTypeId);
    List<Room> findRoomsById(Long classTypeId);
    List<FitnessClass> findFitnessClassesById(Long classTypeId);
    List<ClassType> readAllByName(String name);
}
