package cz.cvut.fit.tjv.fitnessApp.service;

import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.domain.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.repository.ClassTypeRepository;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
public class ClassTypeServiceImpl extends CrudServiceImpl<ClassType, Integer> implements ClassTypeService {

    private final ClassTypeRepository classTypeRepository;

    @Autowired
    public ClassTypeServiceImpl(ClassTypeRepository classTypeRepository) {
        this.classTypeRepository = classTypeRepository;
    }

    @Override
    protected ClassTypeRepository getRepository() {
        return classTypeRepository;
    }

    @Override
    public Set<Instructor> findInstructorsByClassType(Integer classTypeId) {
        return classTypeRepository.findById(classTypeId)
                .map(ClassType::getInstructors)
                .orElse(Collections.emptySet());
    }

    @Override
    public Set<FitnessClass> findFitnessClassesByClassType(Integer classTypeId) {
        return classTypeRepository.findById(classTypeId)
                .map(ClassType::getClasses)
                .orElse(Collections.emptySet());
    }

    @Override
    public Set<Room> findRoomsByClassType(Integer classTypeId) {
        return classTypeRepository.findById(classTypeId)
                .map(ClassType::getRooms)
                .orElse(Collections.emptySet());
    }

    @Override
    public Set<ClassType> readAllByName(String name) {
        return new HashSet<>(classTypeRepository.findByNameContainingIgnoreCase(name));
    }
}