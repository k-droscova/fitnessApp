package cz.cvut.fit.tjv.fitnessApp.controller.mappers;

import cz.cvut.fit.tjv.fitnessApp.controller.dto.RoomDto;
import cz.cvut.fit.tjv.fitnessApp.domain.Room;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {FitnessClassMapper.class, ClassTypeMapper.class})
public interface RoomMapper {
    RoomDto toDto(Room room);
    Room toEntity(RoomDto roomDto);
}