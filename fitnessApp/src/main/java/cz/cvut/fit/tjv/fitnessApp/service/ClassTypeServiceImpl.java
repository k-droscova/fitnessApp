package cz.cvut.fit.tjv.fitnessApp.service;

import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import cz.cvut.fit.tjv.fitnessApp.domain.Room;
import cz.cvut.fit.tjv.fitnessApp.repository.ClassTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ClassTypeServiceImpl extends CrudServiceImpl<ClassType, Long> implements ClassTypeService {

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
    public List<Instructor> findInstructorsById(Long classTypeId) {
        return classTypeRepository.findById(classTypeId)
                .map(ClassType::getInstructors)
                .orElse(Collections.emptyList());
    }

    @Override
    public List<FitnessClass> findFitnessClassesById(Long classTypeId) {
        return classTypeRepository.findById(classTypeId)
                .map(ClassType::getClasses)
                .orElse(Collections.emptyList());
    }

    @Override
    public List<Room> findRoomsById(Long classTypeId) {
        return classTypeRepository.findById(classTypeId)
                .map(ClassType::getRooms)
                .orElse(Collections.emptyList());
    }

    @Override
    public List<ClassType> readAllByName(String name) {
        return new ArrayList<>(classTypeRepository.findByNameContainingIgnoreCase(name));
    }
}