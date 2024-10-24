package cz.cvut.fit.tjv.fitnessApp.service;

import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.domain.Room;

import java.util.Set;

public interface ClassTypeService extends CrudService<ClassType, Integer> {
    Set<Instructor> findInstructorsByClassType(Integer classTypeId);
    Set<Room> findRoomsByClassType(Integer classTypeId);
    Set<FitnessClass> findFitnessClassesByClassType(Integer classTypeId);
    Set<ClassType> readAllByName(String name);
}
