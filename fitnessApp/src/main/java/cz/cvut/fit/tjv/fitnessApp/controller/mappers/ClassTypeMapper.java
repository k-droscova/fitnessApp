package cz.cvut.fit.tjv.fitnessApp.controller.mappers;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.ClassTypeDto;
import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {InstructorMapper.class, RoomMapper.class, FitnessClassMapper.class})
public interface ClassTypeMapper {
    ClassTypeMapper INSTANCE = Mappers.getMapper(ClassTypeMapper.class);
    ClassTypeDto toDto(ClassType classType);
    ClassType toEntity(ClassTypeDto classTypeDto);
}
