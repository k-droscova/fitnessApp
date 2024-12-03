package cz.cvut.fit.tjv.fitnessApp.service.mappers;

import java.util.List;

public interface EntityMapper <Entity, DTO>{

    public Entity convertToEntity(DTO dto);

    public DTO convertToDto(Entity entity);

    public List<DTO> convertManyToDto(List<Entity> entities);
}
