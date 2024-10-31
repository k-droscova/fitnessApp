package cz.cvut.fit.tjv.fitnessApp.controller.mappers;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.TraineeDto;
import cz.cvut.fit.tjv.fitnessApp.domain.Trainee;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {FitnessClassMapper.class})
public interface TraineeMapper {
    TraineeDto toDto(Trainee trainee);
    Trainee toEntity(TraineeDto traineeDto);
}