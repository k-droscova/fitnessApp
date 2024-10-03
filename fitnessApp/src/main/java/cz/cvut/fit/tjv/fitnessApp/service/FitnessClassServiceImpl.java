package cz.cvut.fit.tjv.fitnessApp.service;

import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.respository.CrudRepository;
import cz.cvut.fit.tjv.fitnessApp.respository.FitnessClassRepository;

public class FitnessClassServiceImpl extends CrudServiceImpl<FitnessClass, Integer> implements FitnessClassService {
    private FitnessClassRepository fitnessClassRepository;
    @Override
    protected CrudRepository<FitnessClass, Integer> getRepository() {
        return fitnessClassRepository;
    }
}
