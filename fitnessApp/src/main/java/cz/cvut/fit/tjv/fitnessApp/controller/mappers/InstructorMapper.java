package cz.cvut.fit.tjv.fitnessApp.controller.mappers;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.InstructorDto;
import cz.cvut.fit.tjv.fitnessApp.domain.Instructor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {ClassTypeMapper.class, FitnessClassMapper.class})
public interface InstructorMapper {
    InstructorMapper INSTANCE = Mappers.getMapper(InstructorMapper.class);
    InstructorDto toDto(Instructor instructor);
    Instructor toEntity(InstructorDto instructorDto);
}