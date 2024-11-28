package cz.cvut.fit.tjv.fitnessApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.repository.FitnessClassRepository;
import org.springframework.data.repository.CrudRepository;

@Service
public class FitnessClassServiceImpl extends CrudServiceImpl<FitnessClass, Long> implements FitnessClassService {

    private final FitnessClassRepository fitnessClassRepository;

    @Autowired
    public FitnessClassServiceImpl(FitnessClassRepository fitnessClassRepository) {
        this.fitnessClassRepository = fitnessClassRepository;
    }

    @Override
    protected CrudRepository<FitnessClass, Long> getRepository() {
        return fitnessClassRepository;
    }
}