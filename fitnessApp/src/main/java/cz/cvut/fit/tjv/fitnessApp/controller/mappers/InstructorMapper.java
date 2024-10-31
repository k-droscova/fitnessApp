package cz.cvut.fit.tjv.fitnessApp.controller.mappers;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.InstructorDto;
import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {ClassTypeMapper.class, FitnessClassMapper.class})
public interface InstructorMapper {
    InstructorDto toDto(Instructor instructor);
    Instructor toEntity(InstructorDto instructorDto);
}