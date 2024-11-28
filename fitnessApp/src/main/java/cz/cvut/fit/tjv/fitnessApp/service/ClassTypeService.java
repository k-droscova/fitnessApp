package cz.cvut.fit.tjv.fitnessApp.service;

import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.domain.Room;

import java.util.List;

public interface ClassTypeService extends CrudService<ClassType, Long> {
    List<Instructor> findInstructorsByClassType(Long classTypeId);
    List<Room> findRoomsByClassType(Long classTypeId);
    List<FitnessClass> findFitnessClassesByClassType(Long classTypeId);
    List<ClassType> readAllByName(String name);
}
