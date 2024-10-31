package cz.cvut.fit.tjv.fitnessApp.controller.mappers;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.ClassTypeDto;
import cz.cvut.fit.tjv.fitnessApp.domain.ClassType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {InstructorMapper.class, RoomMapper.class, FitnessClassMapper.class})
public interface ClassTypeMapper {
    ClassTypeDto toDto(ClassType classType);
    ClassType toEntity(ClassTypeDto classTypeDto);
}
