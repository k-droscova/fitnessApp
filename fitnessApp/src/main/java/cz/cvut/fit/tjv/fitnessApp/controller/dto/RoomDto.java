package cz.cvut.fit.tjv.fitnessApp.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class RoomDto {
    private Integer id;
    private int maxCapacity;
    private Set<FitnessClassDto> classes;
    private Set<ClassTypeDto> classTypes;

    public RoomDto(Integer id, int maxCapacity, Set<FitnessClassDto> classes, Set<ClassTypeDto> classTypes) {
        this.id = id;
        this.maxCapacity = maxCapacity;
        this.classes = classes;
        this.classTypes = classTypes;
    }
}