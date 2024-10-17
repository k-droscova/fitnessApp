package cz.cvut.fit.tjv.fitnessApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import cz.cvut.fit.tjv.fitnessApp.repository.ClassTypeRepository;

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
}