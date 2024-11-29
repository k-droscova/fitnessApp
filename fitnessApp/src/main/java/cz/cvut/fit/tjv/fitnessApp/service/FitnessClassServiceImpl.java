package cz.cvut.fit.tjv.fitnessApp.service;

import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import cz.cvut.fit.tjv.fitnessApp.domain.Trainee;
import cz.cvut.fit.tjv.fitnessApp.repository.FitnessClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

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

    @Override
    public List<Trainee> findTraineesById(Long fitnessClassId) {
        return fitnessClassRepository.findById(fitnessClassId)
                .map(FitnessClass::getTrainees)
                .orElse(Collections.emptyList());
    }

    @Override
    public List<FitnessClass> readAllByDate(LocalDate date) {
        return fitnessClassRepository.findFitnessClassesByDate(date);
    }

    @Override
    public List<FitnessClass> readAllByDateAndTimeBetween(LocalDate date, LocalTime start, LocalTime end) {
        return fitnessClassRepository.findFitnessClassesByTimeBetweenAndDate(start, end, date);
    }

    @Override
    public List<FitnessClass> readAllByDateAndRoomId(LocalDate date, Long roomId) {
        return fitnessClassRepository.findFitnessClassesByDateAndRoom_Id(date, roomId);
    }
}