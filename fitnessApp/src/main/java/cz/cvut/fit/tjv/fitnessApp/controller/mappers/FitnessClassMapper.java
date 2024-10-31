package cz.cvut.fit.tjv.fitnessApp.controller.mappers;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.FitnessClassDto;
import cz.cvut.fit.tjv.fitnessApp.domain.FitnessClass;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {InstructorMapper.class, RoomMapper.class, ClassTypeMapper.class, TraineeMapper.class})
public interface FitnessClassMapper {
    FitnessClassMapper INSTANCE = Mappers.getMapper(FitnessClassMapper.class);

    FitnessClassDto toDto(FitnessClass fitnessClass);

    FitnessClass toEntity(FitnessClassDto fitnessClassDto);
}