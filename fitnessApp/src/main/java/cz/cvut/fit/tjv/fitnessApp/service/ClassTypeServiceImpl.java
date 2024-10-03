package cz.cvut.fit.tjv.fitnessApp.service;

import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.respository.ClassTypeRepository;
import cz.cvut.fit.tjv.fitnessApp.respository.CrudRepository;

public class ClassTypeServiceImpl extends CrudServiceImpl<ClassType, Integer> implements ClassTypeService {
    private ClassTypeRepository classTypeRepository;
    @Override
    protected CrudRepository<ClassType, Integer> getRepository() {
        return classTypeRepository;
    }
}