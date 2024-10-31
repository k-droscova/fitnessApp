package cz.cvut.fit.tjv.fitnessApp.controller.mappers;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.FitnessClassDto;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {InstructorMapper.class, RoomMapper.class, ClassTypeMapper.class, TraineeMapper.class})
public interface FitnessClassMapper {
    FitnessClassDto toDto(FitnessClass fitnessClass);
    FitnessClass toEntity(FitnessClassDto fitnessClassDto);
}